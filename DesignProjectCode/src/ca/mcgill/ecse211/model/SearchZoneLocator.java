package ca.mcgill.ecse211.model;

/**
 * This class handles the robot's navigation from the starting corner
 * to the search zone after localization is complete.
 * 
 * @author Carlo D'Angelo
 */
public class SearchZoneLocator {

	private Odometer odo;
	private final double TILE_SIZE = Navigation.TILE_SIZE;
	private int startingCorner, homeZoneLLX, homeZoneLLY, homeZoneURX, homeZoneURY,
					tunnelLLX, tunnelLLY, tunnelURX, tunnelURY;
	private int islandLLX, islandLLY, islandURX, islandURY,
					searchZoneLLX, searchZoneLLY, searchZoneURX, searchZoneURY;
	private LightLocalizer lightLocalizer;
	private Navigation navigator;
	private Clamp clamp;
	
	/**
	 * This is the default constructor of this class.
	 * @param robot instance of Robot class
	 * @param lightLocalizer instance of LightLocalizer class
	 * @param navigator instance of Navigation class
	 * @throws OdometerExceptions
	 */
	public SearchZoneLocator(Robot robot, LightLocalizer lightLocalizer, 
								Clamp clamp, Navigation navigator) throws OdometerExceptions {
		odo = Odometer.getOdometer();
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
		this.navigator= navigator;
		this.clamp = clamp;
	}
	
	/** 
	 * Method that allows the robot to make its way to the search zone.
	 * The path the robot takes will depend on the starting corner (startingCorner) parameter
	 * and where the tunnels are located.
	 */
	public void goToSearchZone(){
		// set new position and new angle after localization
		// current position and current angle will depend on starting corner
		switch(startingCorner){
			case 0: 
				// corner 0
				odo.setXYT((homeZoneLLX + 1) * TILE_SIZE, (homeZoneLLY + 1) * TILE_SIZE, 0.0);
				clamp.grabCan();
				if (homeZoneURX < islandLLX) { // horizontal tunnel
					if(homeZoneLLY == tunnelLLY) {
						if (tunnelLLX - 1 == homeZoneLLX + 1) {
							navigator.turnTo(-180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}else {
							navigator.travelTo(tunnelLLX - 1, tunnelURY);
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelURY);
							navigator.turnTo(-180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}
					} else {
						if (tunnelLLX - 1 == homeZoneLLX + 1) {
							if ((homeZoneLLY + 1) == tunnelLLY) {
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}else {
								navigator.travelTo(tunnelLLX - 1, tunnelLLY);
								navigator.turnTo(45);
								lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelLLY);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}
						}else {
							navigator.travelTo(tunnelLLX - 1, tunnelLLY);
							if ((homeZoneLLY + 1) == tunnelLLY) {
								navigator.turnTo(-45);
							}
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelLLY);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}

					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					double yComponent;
					if (tunnelURY == islandURY) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelURX + 1, yComponent = tunnelLLY);
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelURX + 1, yComponent = tunnelURY);
					}
					if (!(tunnelURX + 1 == searchZoneLLX && yComponent == searchZoneLLY)) {
						navigator.travelTo(searchZoneLLX, searchZoneLLY);
						if (yComponent == searchZoneLLY) {
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (yComponent < searchZoneLLY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(45);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (yComponent > searchZoneLLY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						}
					}

				} else if (homeZoneURY < islandLLY) { // vertical tunnel
					if(homeZoneLLX == tunnelLLX) {
						if (tunnelLLY - 1 == homeZoneLLY + 1) {
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}else {
							navigator.travelTo(tunnelURX, tunnelLLY - 1);
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(tunnelURX, tunnelLLY - 1);
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}
					} else {
						if (tunnelLLY - 1 == homeZoneLLY + 1) {
							if ((homeZoneLLX + 1) == tunnelLLX) {
								navigator.turnTo(90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}else {
								navigator.travelTo(tunnelLLX, tunnelLLY - 1);
								navigator.turnTo(-45);
								lightLocalizer.lightLocalize(tunnelLLX, tunnelLLY - 1);
								navigator.turnTo(90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}
						}else {
							navigator.travelTo(tunnelLLX, tunnelLLY - 1);
							if ((homeZoneLLX + 1) == tunnelLLX) {
								navigator.turnTo(45);
							}
							lightLocalizer.lightLocalize(tunnelLLX, tunnelLLY - 1);
							navigator.turnTo(90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}

					}
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					double xComponent;
					if (tunnelURX == islandURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(xComponent = tunnelLLX, tunnelURY + 1);
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(xComponent = tunnelURX, tunnelURY + 1);
					}
					if (!(xComponent == searchZoneLLX && tunnelURY + 1 == searchZoneLLY)) {
						navigator.travelTo(searchZoneLLX, searchZoneLLY);
						if (xComponent == searchZoneLLX) {
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (xComponent < searchZoneLLX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(-45);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (xComponent > searchZoneLLX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						}
					}

				}
				break;
			case 1: 
				// corner 1
				odo.setXYT((homeZoneURX - 1) * TILE_SIZE, (homeZoneLLY + 1) * TILE_SIZE, 270.0);
				clamp.grabCan();
				if (homeZoneLLX > islandURX) { // horizontal tunnel
					if(homeZoneLLY == tunnelLLY) {
						if (tunnelURX + 1 == homeZoneURX - 1) {
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}else {
							navigator.travelTo(tunnelURX + 1, tunnelURY);
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelURY);
							navigator.turnTo(180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}
					} else {
						if (tunnelURX + 1 == homeZoneURX - 1) {
							if ((homeZoneLLY + 1) == tunnelLLY) {
								navigator.turnTo(90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}else {
								navigator.travelTo(tunnelURX + 1, tunnelLLY);
								navigator.turnTo(45);
								lightLocalizer.lightLocalize(tunnelURX + 1, tunnelLLY);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}
						}else {
							navigator.travelTo(tunnelURX + 1, tunnelLLY);
							if ((homeZoneLLY + 1) == tunnelLLY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelLLY);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}

					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					double yComponent;
					if (tunnelURY == islandURY) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelLLX - 1, yComponent = tunnelLLY);
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelLLX - 1, yComponent = tunnelURY);
					}
					if (!(tunnelLLX - 1 == searchZoneURX && yComponent == searchZoneURY)) {
						navigator.travelTo(searchZoneURX, searchZoneURY);
						if (yComponent == searchZoneURY) {
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (yComponent < searchZoneURY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(45);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (yComponent > searchZoneURY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-180);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						}
					}

				} else if (homeZoneURY < islandLLY) { // vertical tunnel
					if(homeZoneURX == tunnelURX) {
						if (tunnelLLY - 1 == homeZoneLLY + 1) {
							navigator.turnTo(180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}else {
							navigator.travelTo(tunnelLLX, tunnelLLY - 1);
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(tunnelLLX,  tunnelLLY - 1);
							navigator.turnTo(90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}
					} else {
						if (tunnelLLY - 1 == homeZoneLLY + 1) {
							if ((homeZoneURX - 1) == tunnelURX) {
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}else {
								navigator.travelTo(tunnelURX, tunnelLLY - 1);
								navigator.turnTo(135);
								lightLocalizer.lightLocalize(tunnelURX, tunnelLLY - 1);
								navigator.turnTo(-90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}
						}else {
							navigator.travelTo(tunnelURX, tunnelLLY - 1);
							if ((homeZoneURX - 1) == tunnelURX) {
								navigator.turnTo(45);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(tunnelURX, tunnelLLY - 1);
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}

					}					
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					double xComponent;
					if (tunnelURX == islandURX) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(xComponent = tunnelLLX, tunnelURY + 1);
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(xComponent = tunnelURX, tunnelURY + 1);
					}
					if (!(xComponent == searchZoneLLX && tunnelURY + 1 == searchZoneLLY)) {
						navigator.travelTo(searchZoneLLX, searchZoneLLY);
						if (xComponent == searchZoneLLX) {
							navigator.turnTo(45);
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (xComponent < searchZoneLLX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(-45);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (xComponent > searchZoneLLX) {
							if (tunnelURY + 1 == searchZoneLLY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						}
					}

				}
				break;
			case 2: 
				// corner 2
				odo.setXYT((homeZoneURX - 1) * TILE_SIZE, (homeZoneURY - 1) * TILE_SIZE, 180.0);
				clamp.grabCan();
				if (homeZoneLLX > islandURX) { // horizontal tunnel
					if(homeZoneURY == tunnelURY) {
						if (tunnelURX + 1 == homeZoneURX - 1) {
							navigator.turnTo(180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}else {
							navigator.travelTo(tunnelURX + 1, tunnelLLY);
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelLLY);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}
					} else {
						if (tunnelURX + 1 == homeZoneURX - 1) {
							if ((homeZoneURY - 1) == tunnelURY) {
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}else {
								navigator.travelTo(tunnelURX + 1, tunnelURY);
								navigator.turnTo(-135);
								lightLocalizer.lightLocalize(tunnelURX + 1, tunnelURY);
								navigator.turnTo(180);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}
						}else {
							navigator.travelTo(tunnelURX + 1, tunnelURY);
							if ((homeZoneURY - 1) == tunnelURY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(180);
							}
							lightLocalizer.lightLocalize(tunnelURX + 1, tunnelURY);
							navigator.turnTo(180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}

					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					double yComponent;
					if (tunnelURY == islandURY) {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelLLX - 1, yComponent = tunnelLLY);
					} else {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelLLX - 1, yComponent = tunnelURY);
					}
					if (!(tunnelLLX - 1 == searchZoneURX && yComponent == searchZoneURY)) {
						navigator.travelTo(searchZoneURX, searchZoneURY);
						if (yComponent == searchZoneURY) {
							navigator.turnTo(135);
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (yComponent < searchZoneURY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(45);
							}else {
								navigator.turnTo(90);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (yComponent > searchZoneURY) {
							if (tunnelLLX - 1 == searchZoneURX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(180);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						}
					}
					
				} else if (homeZoneLLY > islandURY) { // vertical tunnel
					if(homeZoneURX == tunnelURX) {
						if (tunnelURY + 1 == homeZoneURY - 1) {
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}else {
							navigator.travelTo(tunnelLLX, tunnelURY + 1);
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(tunnelLLX,  tunnelURY + 1);
							navigator.turnTo(90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}
					} else {
						if (tunnelURY + 1 == homeZoneURY - 1) {
							if ((homeZoneURX - 1) == tunnelURX) {
								navigator.turnTo(90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}else {
								navigator.travelTo(tunnelURX, tunnelURY + 1);
								navigator.turnTo(135);
								lightLocalizer.lightLocalize(tunnelURX, tunnelURY + 1);
								navigator.turnTo(-90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}
						}else {
							navigator.travelTo(tunnelURX, tunnelURY + 1);
							if ((homeZoneURX - 1) == tunnelURX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-180);
							}
							lightLocalizer.lightLocalize(tunnelURX, tunnelURY + 1);
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}
					}	
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					double xComponent;
					if (tunnelURX == islandURX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(xComponent = tunnelLLX, tunnelLLY - 1);
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(xComponent = tunnelURX, tunnelLLY - 1);
					}
					if (!(xComponent == searchZoneURX && tunnelLLY - 1 == searchZoneURY)) {
						navigator.travelTo(searchZoneURX, searchZoneURY);
						if (xComponent == searchZoneURX) {
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (xComponent < searchZoneURX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(-45);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (xComponent > searchZoneURX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(-180);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						}
					}
				}
				break;
			case 3:	
				// corner 3
				odo.setXYT((homeZoneLLX + 1) * TILE_SIZE, (homeZoneURY - 1) * TILE_SIZE, 90.0);
				clamp.grabCan();
				if (homeZoneURX < islandLLX) { // horizontal tunnel
					if(homeZoneURY == tunnelURY) {
						if (tunnelLLX - 1 == homeZoneLLX + 1) {
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}else {
							navigator.travelTo(tunnelLLX - 1, tunnelLLY);
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelLLY);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}
					} else {
						if (tunnelLLX - 1 == homeZoneLLX + 1) {
							if ((homeZoneURY - 1) == tunnelURY) {
								navigator.turnTo(90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}else {
								navigator.travelTo(tunnelLLX - 1, tunnelURY);
								navigator.turnTo(-135);
								lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelURY);
								navigator.turnTo(180);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(-90);
							}
						}else {
							navigator.travelTo(tunnelLLX - 1, tunnelURY);
							if ((homeZoneURY - 1) == tunnelURY) {
								navigator.turnTo(-45);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(tunnelLLX - 1, tunnelURY);
							navigator.turnTo(180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}
					}
					// drive through tunnel
					navigator.driveForward(((tunnelURX - tunnelLLX) + 2) * TILE_SIZE);
					double yComponent;
					if (tunnelURY == islandURY) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-135);
						lightLocalizer.lightLocalize(tunnelURX + 1, yComponent = tunnelLLY);
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(45);
						lightLocalizer.lightLocalize(tunnelURX + 1, yComponent = tunnelURY);
					}
					if (!(tunnelURX + 1 == searchZoneLLX && yComponent == searchZoneLLY)) {
						navigator.travelTo(searchZoneLLX, searchZoneLLY);
						if (yComponent == searchZoneLLY) {
							navigator.turnTo(-45);
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (yComponent < searchZoneLLY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(45);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						} else if (yComponent > searchZoneLLY) {
							if (tunnelURX + 1 == searchZoneLLX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(searchZoneLLX, searchZoneLLY);
						}
					}
					
				} else if (homeZoneLLY > islandURY) { // vertical tunnel
					if(homeZoneLLX == tunnelLLX) {
						if (tunnelURY + 1 == homeZoneURY - 1) {
							navigator.turnTo(-180);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}else {
							navigator.travelTo(tunnelURX, tunnelURY + 1);
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(tunnelURX,  tunnelURY + 1);
							navigator.turnTo(-90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(-90);
						}
					} else {
						if (tunnelURY + 1 == homeZoneURY - 1) {
							if ((homeZoneLLX + 1) == tunnelLLX) {
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}else {
								navigator.travelTo(tunnelLLX, tunnelURY + 1);
								navigator.turnTo(-45);
								lightLocalizer.lightLocalize(tunnelLLX, tunnelURY + 1);
								navigator.turnTo(90);
								navigator.driveForward(0.5 * TILE_SIZE);
								navigator.turnTo(90);
							}
						}else {
							navigator.travelTo(tunnelLLX, tunnelURY + 1);
							if ((homeZoneLLX + 1) == tunnelLLX) {
								navigator.turnTo(-135);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(tunnelLLX, tunnelURY + 1);
							navigator.turnTo(90);
							navigator.driveForward(0.5 * TILE_SIZE);
							navigator.turnTo(90);
						}
					}	
					// drive through tunnel
					navigator.driveForward(((tunnelURY - tunnelLLY) + 2) * TILE_SIZE);
					double xComponent;
					if (tunnelURX == islandURX) {
						navigator.turnTo(90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(135);
						lightLocalizer.lightLocalize(xComponent = tunnelLLX, tunnelLLY - 1);
					} else {
						navigator.turnTo(-90);
						navigator.driveForward(0.5 * TILE_SIZE);
						navigator.turnTo(-45);
						lightLocalizer.lightLocalize(xComponent = tunnelURX, tunnelLLY - 1);
					}
					if (!(xComponent == searchZoneURX && tunnelLLY - 1 == searchZoneURY)) {
						navigator.travelTo(searchZoneURX, searchZoneURY);
						if (xComponent == searchZoneURX) {
							navigator.turnTo(-135);
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (xComponent < searchZoneURX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(-45);
							}else {
								navigator.turnTo(-90);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						} else if (xComponent > searchZoneURX) {
							if (tunnelLLY - 1 == searchZoneURY) {
								navigator.turnTo(135);
							}else {
								navigator.turnTo(-180);
							}
							lightLocalizer.lightLocalize(searchZoneURX, searchZoneURY);
						}
					}
				}
				break;
		    default:
		    	System.out.println("Error - invalid button"); // None of the above - abort
		        System.exit(-1);
		        break;
		}
		clamp.offloadCan();
		
	} 
  
}