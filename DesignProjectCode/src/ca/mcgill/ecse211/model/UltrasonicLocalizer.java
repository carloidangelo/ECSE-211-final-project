package ca.mcgill.ecse211.model;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * This class handles the ultrasonic localization of the robot.
 * 
 * @author Carlo D'Angelo
 */
public class UltrasonicLocalizer {

	/**
	 * Rotation speed (in degrees/second) of the robot during the ultrasonic localization.
	 */
	private static final int ROTATION_SPEED = 150;
	
	/**
	 * As the robot spins, the ultrasonic sensor detects this distance (cm) twice
	 * and, based on the odometer's recorded angles at those instances, calculations 
	 * are made to make the robot face North (i.e., 0 degrees).
	 */
	private static final double CRITICAL_DISTANCE = 30.00;
	
	/**
	 * Threshold (+/-: cm) for the CRITICAL_DISTANCE field.
	 */
	private static final double NOISE_MARGIN = 5.00;

	private static final double TURN_ERROR = 18;
  
	private final double RADIUS = Robot.WHEEL_RAD;
	private final double TRACK = Robot.TRACK;
 
	private Odometer odo;
	private SampleProvider usDistance;
	private float[] usData;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	/**
	 * This is the default constructor of this class.
	 * @param leftMotor left motor of robot
	 * @param rightMotor right motor of robot
	 * @param usDistance sample provider from which to fetch ultrasonic sensor data
	 * @param usData array in which to store the ultrasonic sensor data
	 * @throws OdometerExceptions
	 */
	public UltrasonicLocalizer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
		SampleProvider usDistance, float[] usData) throws OdometerExceptions {
		odo = Odometer.getOdometer();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.usDistance = usDistance;
		this.usData = usData;

		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
	}
	
	/**
	 * Method that allows the robot to perform falling edge localization.
	 */
	public void fallingEdge() {

		double angleA, angleB, turningAngle = 0;

		// Get first angle
		while (readUSDistance() < CRITICAL_DISTANCE + NOISE_MARGIN) {
			leftMotor.forward();
			rightMotor.backward();
		}
		
		while (readUSDistance() > CRITICAL_DISTANCE) {
			leftMotor.forward();
			rightMotor.backward();
		}
		
		angleA = odo.getXYT()[2];

		// Get second angle
		while (readUSDistance() < CRITICAL_DISTANCE + NOISE_MARGIN) {
			leftMotor.backward();
			rightMotor.forward();
		}

		while (readUSDistance() > CRITICAL_DISTANCE) {
			leftMotor.backward();
			rightMotor.forward();
		}
		angleB = odo.getXYT()[2];

		leftMotor.stop(true);
		rightMotor.stop();

		// Calculation of angle that makes robot's heading face 0 degrees
		if (angleA < angleB) {
			turningAngle = (360 - angleB) + ((angleA + angleB) / 2) - 225 + TURN_ERROR;

		} else if (angleA > angleB) {
			turningAngle = -angleB + (angleA + angleB) / 2 - 45 + TURN_ERROR;
		}

		leftMotor.rotate(Navigation.convertAngle(RADIUS, TRACK, turningAngle), true);
		rightMotor.rotate(-Navigation.convertAngle(RADIUS, TRACK, turningAngle), false);
		odo.setTheta(0.0);

	}
	/**
	 * Method that fetches data from the ultrasonic sensor.
	 * @return distance (cm) from the wall
	 */
	private int readUSDistance() {
		usDistance.fetchSample(usData, 0);
		return (int) (usData[0] * 100);
	}

}