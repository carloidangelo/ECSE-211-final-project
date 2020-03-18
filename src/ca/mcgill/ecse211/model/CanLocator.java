package ca.mcgill.ecse211.model;

import ca.mcgill.ecse211.main.Project;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;


/**
 * This class allows the EV3 to search for cans and identify their colors and weights.
 * NOTE: Please refer to Software Document - Section 3.3 for detailed explanations of methods.
 * 
 * @author Mohamed Samee
 * @author Ketan Rampurkar
 * @author Carlo D'Angelo
 */
public class CanLocator {

	private Odometer odo;
	private Navigation navigator;
	private LightLocalizer lightLocalizer;
	
	private AssessCanColor assessCanColor;
	private AssessCanWeight assessCanWeight;
	private Clamp clamp;
	
	private SampleProvider usDistance;
	private float[] usData;
	
	private final double TILE_SIZE = Navigation.TILE_SIZE;
	private static final double ANGLE_ERROR = 10.0;
	private static final double DISTANCE_ERROR = 4.0;
	private static final double TEST_VALUE = 2.0;
	private static final double TEST_ANGLE_CLOSE = 30.0;
	private static final double TEST_ANGLE_FAR = 45.0;
	private static final double ULTRASONIC_ERROR = 5.0; 
	private static final double ULTRASONIC_CLOSE = 6.0;
	private double canAngle = 0;
	private double canDistance = 0;
	private int ENDX = 0, ENDY = 0;
	private int Cx = 0,Cy = 0; //C variables save the current position of the EV3.
	private boolean loopStop;
	
	/**
	 * SC variables save the (x,y) coordinates for which the EV3
	 * begins its search algorithm. SC can either be LL or UR.
	 */
	private int LLx, LLy, URx, URy, SCx, SCy;

	/**
	 * This is the default constructor of this class.
	 * @param robot instance of the Robot class
	 * @param assessCanColor instance of the AssessCanColor class
	 * @param assessCanWeight instance of the AssessCanWeight class
	 * @param clamp instance of the Clamp class
	 * @param usDistance sample provider from which to fetch ultrasonic sensor data
	 * @param usData array in which to store the ultrasonic sensor data
	 * @param navigator instance of the Navigator class
	 * @param lightLocalizer instance of the LightLocalizer class
	 * @throws OdometerExceptions
	 */
	public CanLocator(Robot robot, AssessCanColor assessCanColor, AssessCanWeight assessCanWeight, Clamp clamp, 
			SampleProvider usDistance, float[] usData, Navigation navigator, LightLocalizer lightLocalizer) throws OdometerExceptions {
		odo = Odometer.getOdometer();
		this.assessCanColor = assessCanColor;
		this.assessCanWeight = assessCanWeight;
		this.navigator = navigator;
		this.lightLocalizer = lightLocalizer; 
		this.clamp = clamp;
		this.usDistance = usDistance;
		this.usData = usData;
		LLx = robot.getSearchZoneLLX();
		LLy = robot.getSearchZoneLLY();
		URx = robot.getSearchZoneURX();
		URy = robot.getSearchZoneURY();
		this.ENDX = LLx+1;
		this.ENDY = LLy;
		this.loopStop = false;
	}
	
	/**
	 * This method runs the algorithm for the can searching.
	 * It drives the EV3 forward and in a square around the search zone and looks for cans.
	 * If a can is detected, it calls for the searchProcess(), otherwise it calls goToNext().
	 * Once it has traveled around the whole zone without finding the correct can it then travels
	 * back to its starting corner.
	 */
	public void runLocator(){
		
		this.loopStop = false;
		//Start corner of the search is lower left corner in this case
		this.SCx = LLx;
		this.SCy = LLy;
		
		this.Cy = LLy;
		this.Cx = LLx;
		
		//If the EV3 has reached the UR of the search zone (because it is closer than the LL), then
		//set the SC to UR and begin the search algorithm from there.
		if((odo.getXYT()[0] > (URx*TILE_SIZE-DISTANCE_ERROR) && odo.getXYT()[0] < (URx*TILE_SIZE+DISTANCE_ERROR) 
	            && odo.getXYT()[1] > (URy*TILE_SIZE-DISTANCE_ERROR) && odo.getXYT()[1] < (URy*TILE_SIZE+DISTANCE_ERROR))){
			
			SCx = URx;
			SCy = URy;
			
			//END corner is right beside the start corner
			this.ENDX = URx-1;
			this.ENDY = URy;
			
			//Saves the current position of the EV3
			Cx = URx;
			Cy = URy;
			
			navigator.turnTo(180);
		}
		
		while (!loopStop) {	//&& true if doesnt
			
			//when EV3 goes full circle with the algorithm
			//and ends where it started, break the loop.
			if(Cx == ENDX && Cy == ENDY) {
				
				if(SCx == LLx && SCy == LLy) {
					
					navigator.turnTo(135);
					lightLocalizer.lightLocalize(Cx,Cy);
					navigator.travelTo(LLx,LLy);
					navigator.turnTo(135);
					lightLocalizer.lightLocalize(LLx,LLy);
						
				}
				
				else if (SCx == URx && SCy == URy) {
					
					navigator.turnTo(-45);
					lightLocalizer.lightLocalize(Cx,Cy);
					navigator.travelTo(URx,URy);
					navigator.turnTo(-45);
					lightLocalizer.lightLocalize(URx,URy);
					
				}
				
				break;
			}			
			
			//checkCan takes 90 degrees as an argument(i.e this is assuming the tile
			//has not been scanned yet, so a full 90 degree turn is required)
			else if(!checkCan(90)){
					
					goToNext();
				
			}
			
			//checks a can in front of it
			else{
				
				searchProcess();
				
			}
			
		}
		
	}
	
	
	/**
	 * searchProcess() runs when the EV3 detects a can. It calls assessCan() to 
	 * identify color and weight. Once done, calls travelToStartCorner() while having the can.
	 */
	private void searchProcess(){            
		assessCan(canDistance = (readUSDistance()-(TEST_VALUE)));
		navigator.driveBack(canDistance);
		travelToStartCorner();
	}
	
	/**
	 * checkCan() returns true if a can was spotted by the ultrasonic sensor within the
	 * range of a tile. Otherwise, it returns false.
	 * @param angle amount (in degrees) that the robot has to scan
	 * @return boolean value representing whether a can was spotted (true) or not (false)
	 */
	private boolean checkCan(double angle){
	
		canAngle = 0;
	    double currentAngle = odo.getXYT()[2];
	    
		//begin rotating to scan for cans 
		navigator.turnToScan(angle);
        double testDistance;
        while ((testDistance = readUSDistance()) > TILE_SIZE - ULTRASONIC_ERROR) {
            
            //keep turning until the distance of the US is less than a tile (i.e a can is detected)
            
            //if the motors finish the 90 degree turn, and no can is found, the method returns false
            if(!(Project.LEFT_MOTOR.isMoving()) || !(Project.RIGHT_MOTOR.isMoving())) {
                
                navigator.turnTo(-90);
                return false;
            }
            
        }
        //if can is found, stop motors and record the angle the can was detected at
        Project.LEFT_MOTOR.stop(true);
        Project.RIGHT_MOTOR.stop();
        if (testDistance > ULTRASONIC_CLOSE) {
        	 navigator.turnTo(TEST_ANGLE_FAR);
        }else {
        	navigator.turnTo(TEST_ANGLE_CLOSE);
        }
        canAngle = odo.getXYT()[2] - currentAngle;
        if(canAngle < -110){
            canAngle = 360+canAngle-(ANGLE_ERROR/2);
        }
        return true;
	}
	

	/**
	 * assessCan() is a method that is called after checkCan(). It 
	 * makes the EV3 beep depending on the color as well as the weight of the can scanned.
	 * @param distance distance(cm) that the robot has to travel to the can
	 */
	private void assessCan(double distance){

		int heavy = 0;
		navigator.driveForward(distance);
		clamp.grabCan();
		navigator.driveBack(2 * TILE_SIZE / 3);
		navigator.driveForwardWeight(2 * TILE_SIZE / 3);
		while(Project.LEFT_MOTOR.isMoving() && Project.RIGHT_MOTOR.isMoving()){
			heavy = heavy | assessCanWeight.run();
		}
		//Beeps depending on the color and weight of the can
		if(heavy == 1){
			
			System.out.println("heavy can");
			
			switch (assessCanColor.run()) {

				case 1: Sound.playTone(500, 1000);
						break;

				case 2: Sound.playTone(500, 1000);
						Sound.pause(100);
						Sound.playTone(500, 1000);
						break;

				case 3: Sound.playTone(500, 1000);
						Sound.pause(100);
						Sound.playTone(500, 1000);
						Sound.pause(100);
						Sound.playTone(500, 1000);
						break;

				case 4: Sound.playTone(500, 1000);
						Sound.pause(100);
						Sound.playTone(500, 1000);
						Sound.pause(100);
						Sound.playTone(500, 1000);
						Sound.pause(100);
						Sound.playTone(500, 1000);
						break;
				default: Sound.playTone(1500, 1000); //this means incorrect identification 
						 break;
			}
		}
		
		else{
		
			System.out.println("light can");
			
			switch (assessCanColor.run()) {

				case 1: Sound.playTone(500, 500);
						break;

				case 2: Sound.playTone(500, 500);
						Sound.pause(100);
						Sound.playTone(500, 500);
						break;

				case 3: Sound.playTone(500, 500);
						Sound.pause(100);
						Sound.playTone(500, 500);
						Sound.pause(100);
						Sound.playTone(500, 500);
						break;

				case 4: Sound.playTone(500, 500);
						Sound.pause(100);
						Sound.playTone(500, 500);
						Sound.pause(100);
						Sound.playTone(500, 500);
						Sound.pause(100);
						Sound.playTone(500, 500);
						break;
				default: Sound.playTone(1500, 500); //this means incorrect identification 
						 break;
			}
			
		}
	} 
	
	/**
	* goToNext() uses travelTo() to drive the EV3 forward to the next position when no cans are detected.
	*/
	private void goToNext() { 

		//keeps coordinate values in check to localize the EV3 whenever required
		if(Cy < URy && Cx==LLx) {
			
			Cy = Cy+1;
			navigator.travelTo(Cx,Cy);
			
		}
		else if(Cx < URx && Cy==URy) { 
			
			Cx = Cx+1;
			navigator.travelTo(Cx,Cy);
		}
		else if(Cy > LLy && Cx==URx) {
			
			Cy=Cy-1;
			navigator.travelTo(Cx,Cy);
		}
		
		//ENDX is the x coordinate of the final position of the search algorithm
		else if(Cx > LLx && Cy==LLy) {
			
			Cx=Cx-1;
			navigator.travelTo(Cx,Cy);
		}
		
		
		//Localization to fix odo when the EV3 has traveled half way across an edge of the SearchZone.
		//Finds midpoint of the edge and compares it to the current EV3 coordinate.
		if((int)((URy-LLy)/2) == (int)Cy) {
		
			if ( (odo.getXYT()[2] >= 360-ANGLE_ERROR) || 
			    	(odo.getXYT()[2] <= 0+ANGLE_ERROR)){
				
				navigator.turnTo(45);
			    lightLocalizer.lightLocalize(Cx,Cy);
				
			}
			
			else if ( (odo.getXYT()[2] >= 180-ANGLE_ERROR) && 
			    	(odo.getXYT()[2] <= 180+ANGLE_ERROR) ){
			
				navigator.turnTo(-135);
				lightLocalizer.lightLocalize(Cx,Cy);
				 navigator.turnTo(180);
				
			}
		}
		
		//The use of Math.round() is so the rounded value is used rather than the floored
		else if ((int)(Math.round((URx-LLx)/2)) == (int)Cx) {
				
			if ( (odo.getXYT()[2] >= 90-ANGLE_ERROR) && 
			    	(odo.getXYT()[2] <= 90+ANGLE_ERROR) ){
				
				navigator.turnTo(-45);
				lightLocalizer.lightLocalize(Cx,Cy);
				navigator.turnTo(90);
				
			}
			
			else if ( (odo.getXYT()[2] >= 270-ANGLE_ERROR) &&
			    	(odo.getXYT()[2] <= 270+ANGLE_ERROR) ){
				
				navigator.turnTo(135);
				lightLocalizer.lightLocalize(Cx,Cy);
				navigator.turnTo(-90);
				
			}
		}
		
		
		//if the EV3 is at one of the 3 corners of the search zone, localize then turn right to stay on the zone border
		if( (Cx*TILE_SIZE > (LLx*TILE_SIZE-DISTANCE_ERROR) && Cx*TILE_SIZE < (LLx*TILE_SIZE+DISTANCE_ERROR) 
		    && Cy*TILE_SIZE > (URy*TILE_SIZE-DISTANCE_ERROR) && Cy*TILE_SIZE < (URy*TILE_SIZE+DISTANCE_ERROR))){
		    
		    navigator.turnTo(45);
		    lightLocalizer.lightLocalize(Cx,Cy);
		    navigator.turnTo(90);
			goToNext();
		    
		}
			  
	    else if ((Cx*TILE_SIZE > (URx*TILE_SIZE-DISTANCE_ERROR) && Cx*TILE_SIZE < (URx*TILE_SIZE+DISTANCE_ERROR) 
	            && Cy*TILE_SIZE > (URy*TILE_SIZE-DISTANCE_ERROR) && Cy*TILE_SIZE < (URy*TILE_SIZE+DISTANCE_ERROR))){
	        
	        navigator.turnTo(-45);
		    lightLocalizer.lightLocalize(Cx,Cy);
		    navigator.turnTo(180);
			goToNext();
	        
	    }
		else if((Cx*TILE_SIZE > (URx*TILE_SIZE-DISTANCE_ERROR) && Cx*TILE_SIZE < (URx*TILE_SIZE+DISTANCE_ERROR) 
		        && Cy*TILE_SIZE > (LLy*TILE_SIZE-DISTANCE_ERROR) && Cy*TILE_SIZE < (LLy*TILE_SIZE+DISTANCE_ERROR))){
					
			navigator.turnTo(-135);
		    lightLocalizer.lightLocalize(Cx,Cy);
		    navigator.turnTo(-90);
			goToNext();
					
		}
		canDistance = 0;
	}
	
	/**
	* travelToStartCorner() is called when a can is found and its weight and color have been identified. This
	* method will use travelTo() from the Navigator class to get the EV3 back to the initial start corner.
	*/
	private void travelToStartCorner() {
		
		navigator.turnTo(-canAngle);
		
		//If the SC was UR, then go to UR
		if (SCx == URx && SCy == URy){
			
			if ( (odo.getXYT()[2] >= 180-ANGLE_ERROR) && 
			    	(odo.getXYT()[2] <= 180+ANGLE_ERROR) ){
			
				if (Cx == URx && Cy == URy) {
					
					navigator.turnTo(-135);
					lightLocalizer.lightLocalize(Cx,Cy);
					
				}

				else {
				
					navigator.turnTo(-135);
					lightLocalizer.lightLocalize(Cx,Cy);
					navigator.travelTo(URx,URy);
					navigator.turnTo(45);
					lightLocalizer.lightLocalize(URx,URy);
				}
			}
			
			else if ( (odo.getXYT()[2] >= 270-ANGLE_ERROR) &&
			    	(odo.getXYT()[2] <= 270+ANGLE_ERROR) ){
			
				navigator.turnTo(135);
				lightLocalizer.lightLocalize(Cx,Cy);			
				navigator.travelTo(URx,LLy);
				navigator.turnTo(-45);
				lightLocalizer.lightLocalize(URx,LLy);
				navigator.travelTo(URx,URy);
				navigator.turnTo(45);
				lightLocalizer.lightLocalize(URx,URy);
			}
			
			else if ( (odo.getXYT()[2] >= 360-ANGLE_ERROR) || 
			    	(odo.getXYT()[2] <= 0+ANGLE_ERROR)){
				
				navigator.turnTo(45);
				lightLocalizer.lightLocalize(Cx,Cy);
				navigator.travelTo(LLx, URy);
				navigator.turnTo(45);
				lightLocalizer.lightLocalize(LLx,URy);
				navigator.travelTo(URx, URy);
				navigator.turnTo(-45);
				lightLocalizer.lightLocalize(URx,URy);
				
			}
			
			else if ( (odo.getXYT()[2] >= 90-ANGLE_ERROR) && 
			    	(odo.getXYT()[2] <= 90+ANGLE_ERROR) ){

				navigator.turnTo(-45);
				lightLocalizer.lightLocalize(Cx,Cy);
				navigator.travelTo(URx,URy);
				navigator.turnTo(-45);
				lightLocalizer.lightLocalize(URx,URy);
				
			}
			
		}
		
		//Otherwise, the SC is always LL
		else {
			
			if ( (odo.getXYT()[2] >= 360-ANGLE_ERROR) || 
			    	(odo.getXYT()[2] <= 0+ANGLE_ERROR)){
				
				if(Cx == LLx && Cy == LLy) {
					
					navigator.turnTo(45);
					lightLocalizer.lightLocalize(Cx,Cy);
					
				}
				
				
				else {
					navigator.turnTo(45);
					lightLocalizer.lightLocalize(Cx,Cy);
					navigator.travelTo(LLx, LLy);
					navigator.turnTo(-135);
					lightLocalizer.lightLocalize(LLx,LLy);
				}

				
			}
			
			else if ( (odo.getXYT()[2] >= 90-ANGLE_ERROR) && 
			    	(odo.getXYT()[2] <= 90+ANGLE_ERROR) ){

				navigator.turnTo(-45);
				lightLocalizer.lightLocalize(Cx,Cy);
				navigator.travelTo(LLx,URy);
				navigator.turnTo(135);
				lightLocalizer.lightLocalize(LLx,URy);
				navigator.travelTo(LLx,LLy);
				navigator.turnTo(-135);
				lightLocalizer.lightLocalize(Cx,Cy);
			}
			
			else if ( (odo.getXYT()[2] >= 180-ANGLE_ERROR) && 
			    	(odo.getXYT()[2] <= 180+ANGLE_ERROR) ){
			
				navigator.turnTo(-135);
				lightLocalizer.lightLocalize(Cx,Cy);
				navigator.travelTo(URx,LLy);
				navigator.turnTo(-135);
				lightLocalizer.lightLocalize(URx,LLy);
				navigator.travelTo(LLx,LLy);
				navigator.turnTo(135);
				lightLocalizer.lightLocalize(LLx,LLy);
			}
			
			else if ( (odo.getXYT()[2] >= 270-ANGLE_ERROR) &&
			    	(odo.getXYT()[2] <= 270+ANGLE_ERROR) ){
			
				navigator.turnTo(135);
				lightLocalizer.lightLocalize(Cx,Cy);			
				navigator.travelTo(LLx,LLy);
				navigator.turnTo(135);
				lightLocalizer.lightLocalize(LLx,LLy);
			}
			
		}
			
		loopStop = true;
	}
	
	/**
	 * Method that fetches data from the ultrasonic sensor.
	 * @return distance (cm) from the wall
	 */
	private int readUSDistance() {
		//this method returns the ultrasonic distance read.
		usDistance.fetchSample(usData, 0);
		return (int) (usData[0] * 100);
		
	}

  
}	

