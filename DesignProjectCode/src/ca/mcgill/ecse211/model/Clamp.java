package ca.mcgill.ecse211.model;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is responsible for driving the clamp motor to load and unload the can.
 * 
 * @author Carlo D'Angelo
 */
public class Clamp {
	
	/**
	 * Amount (in degrees) that the arm rotates to grab the cans.
	 */
	private static final int ROTATE_AMOUNT = 199;
	
	/**
	 * Speed (in degrees/second) of the arm when it grabs the cans.
	 */
	private static final int ROTATION_SPEED = 175;
	private EV3LargeRegulatedMotor clampMotor;
	
	
	/**
	 * This is the default constructor of this class.
	 * @param clampMotor motor that rotates the arm that grasps the can
	 */
	public Clamp(EV3LargeRegulatedMotor clampMotor) {
		this.clampMotor = clampMotor;
	}
	
	/**
	 * This method rotates the motor clockwise to hold onto the can.
	 */
	public void grabCan() {
		clampMotor.setAcceleration(500);
		clampMotor.setSpeed(ROTATION_SPEED);
		clampMotor.rotate(ROTATE_AMOUNT);
	}
	
	/**
	 * This method rotates the motor counter-clockwise to let the can go.
	 */
	public void offloadCan() {
		clampMotor.setAcceleration(500);
		clampMotor.setSpeed(ROTATION_SPEED);
		clampMotor.rotate(-ROTATE_AMOUNT);
	}
}
