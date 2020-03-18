package ca.mcgill.ecse211.model;

import lejos.hardware.Sound;

/**
 * This class handles the robot's navigation from the search zone
 * (i.e., search zone lower left corner or search zone upper right corner) to the
 * staring corner. It also handles the off-loading of the cans in the starting corner.
 * 
 * @author Carlo D'Angelo
 */
public class ReturnHome {
	private final double TILE_SIZE = Navigation.TILE_SIZE;
	
	/**
	 * Distance that the robot travels within the starting corner before off-loading the cans.
	 */
	private static final int CANDROP_DISTANCE = 13;
	private int startingCorner, homeZoneLLX, homeZoneLLY, homeZoneURX, homeZoneURY,
					tunnelLLX, tunnelLLY, tunnelURX, tunnelURY;
	private int islandLLX, islandLLY, islandURX, islandURY,
					searchZoneLLX, searchZoneLLY, searchZoneURX, searchZoneURY;
	private LightLocalizer lightLocalizer;
	private Navigation navigator;
	private Clamp clamp;
	
	/**
	 * This is the default constructor of this class.
	 * @param robot instance of the Robot class
	 * @param lightLocalizer instance of the LightLocalizer class
	 * @param clamp instance of the Clamp class
	 * @param navigator instance of the Navigation class
	 * @throws OdometerExceptions
	 */
	public ReturnHome(Robot robot, LightLocalizer lightLocalizer, 
						Clamp clamp, Navigation navigator) throws OdometerExceptions {
		startingCorner = robot.getStartingCorner();	
		homeZoneLLX = robot.getHomeZoneLLX();
		homeZoneLLY = robot.getHomeZoneLLY();
		homeZoneURX = robot.getHomeZoneURX();
		homeZoneURY = robot.getHomeZoneURY();
		tunnelLLX = robot.getTunnelLLX();
		tunnelLLY = robot.getTunnelLLY();
		tunnelURX = robot.getTunnelURX();
		tunnelURY = robot.getTunnelURY();
		islandLLX = robot.getIslandLLX();
		islandLLY = robot.getIslandLLY();
		islandURX = robot.getIslandURX();
		islandURY = robot.getIslandURY();
		searchZoneLLX = robot.getSearchZoneLLX();
		searchZoneLLY = robot.getSearchZoneLLY();
		searchZoneURX = robot.getSearchZoneURX();
		searchZoneURY = robot.getSearchZoneURY();
		this.lightLocalizer = lightLocalizer;
		this.navigator = navigator;
		this.clamp = clamp;
	}
	
	/**
	 * Method that allows the robot to make its way to the starting corner.
	 * The path the robot takes will depend on the starting corner (startingCorner) parameter
	 * and where the tunnels are located.
	 */
	public void goHome() {
		switch(startingCorner){
			case 0: 
				// go back to corner 0
				if (homeZoneURX < islandLLX) { // horizontal tunnel
					double tunnelY;
					if(islandLLY == tunnelLLY) {
						navigator.travelTo(tunnelURX + 1, tunnelY = tunnelURY);
					} else {
						navigator.travelTo(tunnelURX + 1, tunnelY = tunnelLLY);
					}
					if (!(tunnelURX + 1 == searchZoneLLX && tunnelY == searchZoneLLY)) {
						if (searchZoneLLY == tunnelY) {
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelY);
						} else if (searchZoneLLY < tunnelY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(45);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelY);
						} else if (searchZoneLLY > tunnelY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(180);
							}
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelY);
						}
					}
					if (tunnelY == tunnelURY) {
						navigator.turnTo(180);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}else if (tunnelY == tunnelLLY) {
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					if(homeZoneLLY == tunnelLLY) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelURY);
						if (!(tunnelLLX - 1 == homeZoneLLX + 1 && tunnelURY == homeZoneLLY + 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneLLY + 1);
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneLLY + 1);
						}
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelLLY);
						if (!(tunnelLLX - 1 == homeZoneLLX + 1 && tunnelLLY == homeZoneLLY + 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneLLY + 1);
							if ((homeZoneLLY + 1) == tunnelLLY) {
								navigator.turnTo(135);
							}else if (tunnelLLX - 1 == homeZoneLLX + 1) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(180);
							}
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneLLY + 1);	
						}
					}
					issueOffloadBeeps();
					navigator.turnTo(-135);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					navigator.turnTo(180);
					lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneLLY + 1);
					
				} else if (homeZoneURY < islandLLY){ // vertical tunnel
					double tunnelX;
					if(islandLLX == tunnelLLX) {
						navigator.travelTo(tunnelX = tunnelURX, tunnelURY + 1);
					} else {
						navigator.travelTo(tunnelX = tunnelLLX, tunnelURY + 1);
					}
					if (!(tunnelX == searchZoneLLX && tunnelURY + 1 == searchZoneLLY)) {
						if (searchZoneLLX == tunnelX) {
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(tunnelX, tunnelURY + 1);
						} else if (searchZoneLLX < tunnelX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(-45);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelURY + 1);
						} else if (searchZoneLLX > tunnelX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(-180);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelURY + 1);
						}	
					}
					if (tunnelX == tunnelURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}else if (tunnelX == tunnelLLX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					if(homeZoneLLX == tunnelLLX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(tunnelURX, tunnelLLY - 1);
						if (!(tunnelLLY - 1 == homeZoneLLY + 1 && tunnelURX == homeZoneLLX + 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneLLY + 1);
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneLLY + 1);
						}
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(tunnelLLX, tunnelLLY - 1);
						if (!(tunnelLLY - 1 == homeZoneLLY + 1 && tunnelLLX == homeZoneLLX + 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneLLY + 1);
							if ((homeZoneLLX + 1) == tunnelLLX) {
								navigator.turnTo(-135);
							}else if (tunnelLLY - 1 == homeZoneLLY + 1) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(180);
							}
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneLLY + 1);	
						}
					}
					issueOffloadBeeps();
					navigator.turnTo(-135);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					navigator.turnTo(180);
					lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneLLY + 1);
				}
				break;
			case 1: 
				// go back to corner 1
				if (homeZoneLLX > islandURX) { // horizontal tunnel
					double tunnelY;
					if(islandLLY == tunnelLLY) {
						navigator.travelTo(tunnelLLX - 1, tunnelY = tunnelURY);
					} else {
						navigator.travelTo(tunnelLLX - 1, tunnelY = tunnelLLY);
					}
					if (!(tunnelLLX - 1 == searchZoneURX && tunnelY == searchZoneURY)) {
						if (searchZoneURY == tunnelY) {
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelY);
						} else if (searchZoneURY < tunnelY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(45);
							}
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelY);
						} else if (searchZoneURY > tunnelY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelY);
						}	
					}
					if (tunnelY == tunnelURY) {
						navigator.turnTo(180);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}else if (tunnelY == tunnelLLY) {
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					if(homeZoneLLY == tunnelLLY) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelURX + 1, tunnelURY);
						if (!(tunnelURX + 1 == homeZoneURX - 1 && tunnelURY == homeZoneLLY + 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneLLY + 1);
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneLLY + 1);
						}
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelURX + 1, tunnelLLY);
						if (!(tunnelURX + 1 == homeZoneURX - 1 && tunnelLLY == homeZoneLLY + 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneLLY + 1);
							if ((homeZoneLLY + 1) == tunnelLLY) {
								navigator.turnTo(-45);
							}else if (tunnelURX + 1 ==  homeZoneURX - 1) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneLLY + 1);	
						}
					}
					issueOffloadBeeps();
					navigator.turnTo(135);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					navigator.turnTo(-90);
					lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneLLY + 1);
					navigator.turnTo(-90);
					
				} else if (homeZoneURY < islandLLY){ // vertical tunnel
					double tunnelX;
					if(islandLLX == tunnelLLX) {
						navigator.travelTo(tunnelX = tunnelURX, tunnelURY + 1);
					} else {
						navigator.travelTo(tunnelX = tunnelLLX, tunnelURY + 1);
					}
					if (!(tunnelX == searchZoneLLX && tunnelURY + 1 == searchZoneLLY)) {
						if (searchZoneLLX == tunnelX) {
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(tunnelX, tunnelURY + 1);
						} else if (searchZoneLLX < tunnelX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(-45);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelURY + 1);
						} else if (searchZoneLLX > tunnelX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(-180);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelURY + 1);
						}	
					}
					if (tunnelX == tunnelURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}else if (tunnelX == tunnelLLX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					if(homeZoneURX == tunnelURX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(tunnelLLX, tunnelLLY - 1);
						if (!(tunnelLLX == homeZoneURX - 1 && tunnelLLY - 1 == homeZoneLLY + 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneLLY + 1);
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneLLY + 1);
						}
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(tunnelURX, tunnelLLY - 1);
						if (!(tunnelURX == homeZoneURX - 1 && tunnelLLY - 1 == homeZoneLLY + 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneLLY + 1);
							if ((homeZoneURX - 1) == tunnelURX) {
								navigator.turnTo(-135);
							}else if (tunnelLLY - 1 == homeZoneLLY + 1){
								navigator.turnTo(-45);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneLLY + 1);	
						}

					}
					issueOffloadBeeps();
					navigator.turnTo(135);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					navigator.turnTo(-90);
					lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneLLY + 1);
					navigator.turnTo(-90);
				}
				break;
			case 2: 
				// go back to corner 2
				if (homeZoneLLX > islandURX) { // horizontal tunnel
					double tunnelY;
					if(islandLLY == tunnelLLY) {
						navigator.travelTo(tunnelLLX - 1, tunnelY = tunnelURY);
					} else {
						navigator.travelTo(tunnelLLX - 1, tunnelY = tunnelLLY);
					}
					if (!(tunnelLLX - 1 == searchZoneURX && tunnelY == searchZoneURY)) {
						if (searchZoneURY == tunnelY) {
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelY);
						} else if (searchZoneURY < tunnelY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(45);
							}
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelY);
						} else if (searchZoneURY > tunnelY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelY);
						}	
					}
					if (tunnelY == tunnelURY) {
						navigator.turnTo(-180);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}else if (tunnelY == tunnelLLY) {
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					if(homeZoneURY == tunnelURY) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelURX + 1, tunnelLLY);
						if (!(tunnelURX + 1 == homeZoneURX - 1 && tunnelLLY == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneURY - 1);
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneURY - 1);
						}
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelURX + 1, tunnelURY);
						if (!(tunnelURX + 1 == homeZoneURX - 1 && tunnelURY == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneURY - 1);
							if ((homeZoneURY - 1) == tunnelURY) {
								navigator.turnTo(-45);
							}else if(tunnelURX + 1 == homeZoneURX - 1) {
								navigator.turnTo(45);
							}
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneURY - 1);	
						}
					}
					issueOffloadBeeps();
					navigator.turnTo(45);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneURY - 1);
					navigator.turnTo(180);
					
				} else if (homeZoneLLY > islandURY){ // vertical tunnel
					double tunnelX;
					if(islandLLX == tunnelLLX) {
						navigator.travelTo(tunnelX = tunnelURX, tunnelLLY - 1);
					} else {
						navigator.travelTo(tunnelX = tunnelLLX, tunnelLLY - 1);
					}
					if (!(tunnelX == searchZoneURX && tunnelLLY - 1 == searchZoneURY)) {
						if (searchZoneURX == tunnelX) {
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(tunnelX, tunnelLLY - 1);
						} else if (searchZoneURX < tunnelX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(-45);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelLLY - 1);
						} else if (searchZoneURX > tunnelX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelLLY - 1);
						}	
					}
					if (tunnelX == tunnelURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}else if (tunnelX == tunnelLLX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					if(homeZoneURX == tunnelURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(tunnelLLX, tunnelURY + 1);
						if (!(tunnelLLX == homeZoneURX - 1 && tunnelURY + 1 == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneURY - 1);
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneURY - 1);
						}
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(tunnelURX, tunnelURY + 1);
						if (!(tunnelURX == homeZoneURX - 1 && tunnelURY + 1 == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneURX - 1, homeZoneURY - 1);
							if ((homeZoneURX - 1) == tunnelURX) {
								navigator.turnTo(45);
							}else if (tunnelURY + 1 == homeZoneURY - 1) {
								navigator.turnTo(-45);
							}
							lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneURY - 1);	
						}
					}
					issueOffloadBeeps();
					navigator.turnTo(45);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					lightLocalizer.lightLocalize(homeZoneURX - 1, homeZoneURY - 1);
					navigator.turnTo(180);
				}
				break;
			case 3:	
				// go back to corner 3
				if (homeZoneURX < islandLLX) { // horizontal tunnel
					double tunnelY;
					if(islandLLY == tunnelLLY) {
						navigator.travelTo(tunnelURX + 1, tunnelY = tunnelURY);
					} else {
						navigator.travelTo(tunnelURX + 1, tunnelY = tunnelLLY);
					}
					if (!(tunnelURX + 1 == searchZoneLLX && tunnelY == searchZoneLLY)) {
						if (searchZoneLLY == tunnelY) {
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelY);
						} else if (searchZoneLLY < tunnelY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(45);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelY);
						} else if (searchZoneLLY > tunnelY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(180);
							}
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelY);
						}	
					}
					if (tunnelY == tunnelURY) {
						navigator.turnTo(180);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}else if (tunnelY == tunnelLLY) {
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					if(homeZoneURY == tunnelURY) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelLLY);
						if (!(tunnelLLX - 1 == homeZoneLLX + 1 && tunnelLLY == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneURY - 1);
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneURY - 1);
						}
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelURY);
						if (!(tunnelLLX - 1 == homeZoneLLX + 1 && tunnelURY == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneURY - 1);
							if ((homeZoneURY - 1) == tunnelURY) {
								navigator.turnTo(135);
							}else if (tunnelLLX - 1 == homeZoneLLX + 1) {
								navigator.turnTo(45);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneURY - 1);	
						}
					}
					issueOffloadBeeps();
					navigator.turnTo(-45);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					navigator.turnTo(90);
					lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneURY - 1);
					navigator.turnTo(90);
					
				} else if (homeZoneLLY > islandURY){ // vertical tunnel
					double tunnelX;
					if(islandLLX == tunnelLLX) {
						navigator.travelTo(tunnelX = tunnelURX, tunnelLLY - 1);
					} else {
						navigator.travelTo(tunnelX = tunnelLLX, tunnelLLY - 1);
					}
					if (!(tunnelX == searchZoneURX && tunnelLLY - 1 == searchZoneURY)) {
						if (searchZoneURX == tunnelX) {
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(tunnelX, tunnelLLY - 1);
						} else if (searchZoneURX < tunnelX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(-45);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelLLY - 1);
						} else if (searchZoneURX > tunnelX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(tunnelX, tunnelLLY - 1);
						}	
					}
					if (tunnelX == tunnelURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(90);
					}else if (tunnelX == tunnelLLX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-90);
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					if(homeZoneLLX == tunnelLLX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(tunnelURX, tunnelURY + 1);
						if (!(tunnelURX == homeZoneLLX + 1 && tunnelURY + 1 == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneURY - 1);
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneURY - 1);
						}
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(tunnelLLX, tunnelURY + 1);
						if (!(tunnelLLX == homeZoneLLX + 1 && tunnelURY + 1 == homeZoneURY - 1)) {
							navigator.travelTo(homeZoneLLX + 1, homeZoneURY - 1);
							if ((homeZoneLLX + 1) == tunnelLLX) {
								navigator.turnTo(45);
							}else if (tunnelURY + 1 == homeZoneURY - 1) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneURY - 1);	
						}

					}
					issueOffloadBeeps();
					navigator.turnTo(-45);
					navigator.driveForward(CANDROP_DISTANCE);
					clamp.offloadCan();
					navigator.driveBack(CANDROP_DISTANCE);
					navigator.turnTo(90);
					lightLocalizer.lightLocalize(homeZoneLLX + 1, homeZoneURY - 1);
					navigator.turnTo(90);
				}
				break;
		    default:
		    	System.out.println("Error - invalid button"); // None of the above - abort
		        System.exit(-1);
		        break;
		}
		
	}
	
	/**
	 * Method that delivers the required beeps when the robot arrives at the starting corner.
	 */
	private void issueOffloadBeeps() {
		Sound.beep();
		Sound.pause(100);
		Sound.beep();
		Sound.pause(100);
		Sound.beep();
		Sound.pause(100);
		Sound.beep();
		Sound.pause(100);
		Sound.beep();
	}

}
