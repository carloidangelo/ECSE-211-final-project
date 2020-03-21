package ca.mcgill.ecse211.model;

import lejos.robotics.SampleProvider;

/**
 * This class allows the robot to correctly identify a can's weight by
 * using the touch sensor. 
 * 
 * @author Carlo D'Angelo
 * @author Mohamed Samee
 */

public class AssessCanWeight {

	private float[] tsData;
	private SampleProvider myTouchStatus;
	
	/**
	 * This is the default constructor of this class.
	 * @param tsData array in which to store the touch sensor data
	 * @param myTouchStatus sample provider from which to fetch touch sensor data
	 */
	public AssessCanWeight(float[] tsData, SampleProvider myTouchStatus) {
		this.tsData = tsData;
		this.myTouchStatus = myTouchStatus;
	}
	
	/**
	 * This method directly calls the sampleData() method.
	 * @return touch sensor's fetched data casted to an integer
	 */
	public int run() {
		return (int) sampleData();
	}
	
	/**
	 * This method fetches the data from the touch sensor.
	 * @return data from the touch sensor
	 */
	private float sampleData(){
		myTouchStatus.fetchSample(tsData, 0);
		return tsData[0];
	}
}
