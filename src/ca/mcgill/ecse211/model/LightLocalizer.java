package ca.mcgill.ecse211.model;

import lejos.hardware.Sound;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * This class handles the light localization of the robot.
 * 
 * @author Carlo D'Angelo
 */
public class LightLocalizer {

  /**
   * Rotation speed (in degrees/second) of the robot during the end of
   * the light localization. NOTE: Please refer to Software Document - Section 3.3 
   * for detailed explanations of why we decrease the speed.
   */
  private static final int ROTATION_SPEED_SLOW = 85;
  
  /**
   * Rotation speed (in degrees/second) of the robot during most of
   * the light localization.
   */
  private static final int ROTATION_SPEED = 200;
  private final double TILE_SIZE = Navigation.TILE_SIZE;
  
  /**
   * As the robot spins and the color sensor records the brightness of the playing
   * field, if a percent change in brightness that is greater than this value 
   * (i.e., 20%) is recorded, then the robot knows that a grid line has been detected.
   * NOTE: Please refer to Software Document - Section 3.3 for detailed explanations of 
   * how the light localization process actually works.
   */
  private static final int COLOUR_DIFF = 20;  
  
  /**
   * Distance (cm) between the color sensor and the axis of rotation of the robot.
   */
  private static final double LIGHT_LOC_DISTANCE = 10.2;
  
  /**
   * The robot moves this distance (cm) towards the localization point before
   * starting light localization.
   */
  private static final int EXTRA_DISTANCE = 5;
  private static final double TURN_ERROR = 16.8; 
  
  private Odometer odo;
  private EV3LargeRegulatedMotor leftMotor, rightMotor;
 
  private double[] linePosition;
  private Navigation navigator;
  
  private SampleProvider csLineDetector;
  private float[] csData;

  /**
   * This is the default constructor of this class.
   * @param leftMotor left motor of robot
   * @param rightMotor right motor of robot
   * @param csLineDetector sample provider from which to fetch light sensor data
   * @param csData array in which to receive the light sensor data
   * @param navigator instance of Navigator class
   * @throws OdometerExceptions
   */
  public LightLocalizer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		  				  SampleProvider csLineDetector, float[] csData, Navigation navigator) throws OdometerExceptions {
	odo = Odometer.getOdometer();
	this.leftMotor = leftMotor;
	this.rightMotor = rightMotor;
	linePosition = new double[4];
	this.csLineDetector = csLineDetector;
	this.csData = csData;
	this.navigator = navigator;
	}

  /**
   * Method that allows the robot to perform light localization.
   * @param pointX x coordinate of desired localization point
   * @param pointY y coordinate of desired localization point
   */
  public void lightLocalize(double pointX, double pointY) {
	  
      leftMotor.setSpeed(ROTATION_SPEED);
	  rightMotor.setSpeed(ROTATION_SPEED);
	  
	  int count = 0;
	  float firstReading = readLineDarkness();
	  float sample;
	  while (count < 4) {
		if (count == 3) {
			leftMotor.setSpeed(ROTATION_SPEED_SLOW);
			rightMotor.setSpeed(ROTATION_SPEED_SLOW);
		}
		leftMotor.forward();
		rightMotor.backward();

		sample = readLineDarkness();

		if (100*Math.abs(sample - firstReading)/firstReading > COLOUR_DIFF) {
          linePosition[count] = odo.getXYT()[2];
          Sound.pause(350);
		  count++;
		}
	  }

	  leftMotor.stop(true);
	  rightMotor.stop();

	  double deltaX, deltaY, angleX, angleY, deltaA;

	  if (linePosition[3] > 0 && linePosition[3] < 45) {
		  linePosition[3] += 360; 
	  }
	  
	  angleY = linePosition[3] - linePosition[1];
	  angleX = linePosition[2] - linePosition[0];

	  deltaX = -LIGHT_LOC_DISTANCE * Math.cos(Math.toRadians(angleY / 2));
	  deltaY = -LIGHT_LOC_DISTANCE * Math.cos(Math.toRadians(angleX / 2));
	  
	  deltaA = 90 - (angleY / 2.0) - TURN_ERROR;
	  
	  navigator.turnTo(deltaA);

	  odo.setXYT(pointX * TILE_SIZE + deltaX, pointY * TILE_SIZE + deltaY, 0.0);
	  
	  navigator.travelTo(pointX, pointY);

	  navigator.turnTo(-Navigation.minAng);
	  
	  odo.setXYT(pointX * TILE_SIZE, pointY * TILE_SIZE, 0.0);
	  
	  leftMotor.stop(true);
	  rightMotor.stop();

  }
  
  /**
   * Method that moves the robot closer to the localization point in preparation
   * for the actual light localization.
   */
  public void moveClose() {
	leftMotor.setSpeed(ROTATION_SPEED);
	rightMotor.setSpeed(ROTATION_SPEED);
    navigator.turnTo(45);
    navigator.driveForward(EXTRA_DISTANCE);

  }
  
  /**
   * Method that fetches data from the light sensor.
   * @return darkness (value between 0-1) of what the light sensor is reading multiplied by 1000 
   */
  private float readLineDarkness() {
	  csLineDetector.fetchSample(csData, 0);
	  return csData[0] * 1000;
  }
	
}