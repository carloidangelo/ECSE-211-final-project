package ca.mcgill.ecse211.model;

import java.util.ArrayList;
import java.util.Collections;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class allows the robot to correctly identify a can's color by
 * taking the mode of many samples.
 * 
 * @author Carlo D'Angelo
 */
public class AssessCanColor {
	
	private EV3LargeRegulatedMotor csMotor;
	private ColorClassification csFront;
	
	/**
	 * Amount (in degrees) that the color sensor travels around the can for every 
	 * color classification sample.
	 */
	private static final int ROTATE_AMOUNT = 17;
	
	/**
	 * Amount of color classification samples that the color sensor takes.
	 */
	private static final int ROTATE_COUNT = 9;
	
	/**
	 * Speed (in degrees/second) that the sensor travels around the can.
	 */
	private static final int ROTATION_SPEED = 75;
	
	private static final int TURN_ERROR = 20;
	
	/**
	 * This is the default constructor of this class.
	 * @param csMotor motor in charge of rotating the light sensor that scans the cans
	 * @param csFront instance of the ColorClassification class
	 */
	public AssessCanColor(EV3LargeRegulatedMotor csMotor, ColorClassification csFront) {
		this.csMotor = csMotor;
		this.csFront = csFront;
	}
	
	/**
	 * Method that performs the can detection.
	 * @return integer number between 1-4 that represents the color of the can 
	 */
	public int run() {
		csMotor.setAcceleration(1000);
		csMotor.setSpeed(ROTATION_SPEED);
		ArrayList<String> canColor = new ArrayList<String>();
		ArrayList<Integer> frequency = new ArrayList<Integer>();
		int count = 0;
		while (count < ROTATE_COUNT) {
			csMotor.rotate(-ROTATE_AMOUNT);
			canColor.add(csFront.run());
			count++;
		}
		csMotor.stop();
		csMotor.setAcceleration(1000);
		csMotor.rotate(ROTATE_AMOUNT * ROTATE_COUNT + TURN_ERROR);
		frequency.add(Collections.frequency(canColor, "blue     "));
		frequency.add(Collections.frequency(canColor, "green    "));
		frequency.add(Collections.frequency(canColor, "yellow   "));
		frequency.add(Collections.frequency(canColor, "red      "));
		Integer obj = Collections.max(frequency);
		int index = frequency.indexOf(obj);
		System.out.println(index + 1);
		return index + 1;	
	}
	
}
