package ca.mcgill.ecse211.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import org.json.simple.parser.ParseException;

import ca.mcgill.ecse211.WiFiClient.WifiConnection;
import ca.mcgill.ecse211.main.Project;

/**
 * This class contains the methods that directly handle the game parameters retrieved
 * from the game server. That is, depending the team's assigned color, these methods 
 * will return the corresponding game parameter for that color. For instance, the 
 * getStartingCorner() method will return the RedCorner if the team's color is red and 
 * will return the GreenCorner if the team's color is green. 
 * 
 * @author Carlo D'Angelo
 */
public class Robot {
	
	/**
	 * Robot's wheel radius.
	 */
	public static final double WHEEL_RAD = 2.069;
	
	/**
	 * Distance between the centers of each wheel.
	 */
	public static final double TRACK = 11.305;
	
	private final int TEAM_NUMBER = Project.TEAM_NUMBER;
	
	/**
	 * Stores all of the data from the game server.
	 */
	private Map data;
	
	/**
	 * This is the default constructor of this class.
	 * @param wifi instance of the WifiConnection class
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Robot(WifiConnection wifi) throws UnknownHostException, IOException, ParseException {
		data = wifi.getData();
	}
	
	/**
	 * This method gets the team number associated with the red team.
	 * @return team number associated with the red team.
	 */
	public int getRedTeam() {
		int redTeam = ((Long) data.get("RedTeam")).intValue();
		return redTeam;
	}
	
	/**
	 * This method gets the team number associated with the green team.
	 * @return team number associated with the green team.
	 */
	public int getGreenTeam() {
		int greenTeam = ((Long) data.get("GreenTeam")).intValue();
		return greenTeam;
	}
	
	/**
	 * This method gets the starting corner associated with the team's assigned color.
	 * @return starting corner associated with the team's assigned color
	 */
	public int getStartingCorner() {
		int SC = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			SC = ((Long) data.get("RedCorner")).intValue();
		}else {
			SC = ((Long) data.get("GreenCorner")).intValue();
		}
		return SC;
	}
	
	/**
	 * This method gets the x-coordinate of the starting zone's lower left corner.
	 * The starting zone (Red or Green) depends on the team's assigned color.
	 * @return x-coordinate of the starting zone's lower left corner
	 */
	public int getHomeZoneLLX() {
		int homeZoneLLX = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			homeZoneLLX = ((Long) data.get("Red_LL_x")).intValue();
		}else {
			homeZoneLLX = ((Long) data.get("Green_LL_x")).intValue();
		}
		return homeZoneLLX;
	}
	
	/**
	 * This method gets the y-coordinate of the starting zone's lower left corner.
	 * The starting zone (Red or Green) depends on the team's assigned color.
	 * @return y-coordinate of the starting zone's lower left corner
	 */
	public int getHomeZoneLLY() {
		int homeZoneLLY = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			homeZoneLLY = ((Long) data.get("Red_LL_y")).intValue();
		}else {
			homeZoneLLY = ((Long) data.get("Green_LL_y")).intValue();
		}
		return homeZoneLLY;
	}
	
	/**
	 * This method gets the x-coordinate of the starting zone's upper right corner.
	 * The starting zone (Red or Green) depends on the team's assigned color.
	 * @return x-coordinate of the starting zone's upper right corner
	 */
	public int getHomeZoneURX() {
		int homeZoneURX = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			homeZoneURX = ((Long) data.get("Red_UR_x")).intValue();
		}else {
			homeZoneURX = ((Long) data.get("Green_UR_x")).intValue();
		}
		return homeZoneURX;
	}
	
	/**
	 * This method gets the y-coordinate of the starting zone's upper right corner.
	 * The starting zone (Red or Green) depends on the team's assigned color.
	 * @return y-coordinate of the starting zone's upper right corner
	 */
	public int getHomeZoneURY() {
		int homeZoneURY = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			homeZoneURY = ((Long) data.get("Red_UR_y")).intValue();
		}else {
			homeZoneURY = ((Long) data.get("Green_UR_y")).intValue();
		}
		return homeZoneURY;
	}
	
	/**
	 * This method gets the x-coordinate of the island's lower left corner.
	 * @return x-coordinate of the island's lower left corner
	 */
	public int getIslandLLX() {
		int islandLLX = ((Long) data.get("Island_LL_x")).intValue();
		return islandLLX;
	}
	
	/**
	 * This method gets the y-coordinate of the island's lower left corner.
	 * @return y-coordinate of the island's lower left corner
	 */
	public int getIslandLLY() {
		int islandLLY = ((Long) data.get("Island_LL_y")).intValue();
		return islandLLY;
	}
	
	/**
	 * This method gets the x-coordinate of the island's upper right corner.
	 * @return x-coordinate of the island's upper right corner
	 */
	public int getIslandURX() {
		int islandURX = ((Long) data.get("Island_UR_x")).intValue();
		return islandURX;
	}
	
	/**
	 * This method gets the y-coordinate of the island's upper right corner.
	 * @return y-coordinate of the island's upper right corner
	 */
	public int getIslandURY() {
		int islandURY = ((Long) data.get("Island_UR_y")).intValue();
		return islandURY;
	}
	
	/**
	 * This method gets the x-coordinate of the tunnel's lower left corner.
	 * The tunnel (Red or Green) depends on the team's assigned color.
	 * @return x-coordinate of the tunnel's lower left corner
	 */
	public int getTunnelLLX() {
		int tunnelLLX = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			tunnelLLX = ((Long) data.get("TNR_LL_x")).intValue();
		}else {
			tunnelLLX = ((Long) data.get("TNG_LL_x")).intValue();
		}
		return tunnelLLX;
	}
	
	/**
	 * This method gets the y-coordinate of the tunnel's lower left corner.
	 * The tunnel (Red or Green) depends on the team's assigned color.
	 * @return y-coordinate of the tunnel's lower left corner
	 */
	public int getTunnelLLY() {
		int tunnelLLY = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			tunnelLLY = ((Long) data.get("TNR_LL_y")).intValue();
		}else {
			tunnelLLY = ((Long) data.get("TNG_LL_y")).intValue();
		}
		return tunnelLLY;
	}
	
	/**
	 * This method gets the x-coordinate of the tunnel's upper right corner.
	 * The tunnel (Red or Green) depends on the team's assigned color.
	 * @return x-coordinate of the tunnel's upper right corner
	 */
	public int getTunnelURX() {
		int tunnelURX = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			tunnelURX = ((Long) data.get("TNR_UR_x")).intValue();
		}else {
			tunnelURX = ((Long) data.get("TNG_UR_x")).intValue();
		}
		return tunnelURX;
	}
	
	/**
	 * This method gets the y-coordinate of the tunnel's upper right corner.
	 * The tunnel (Red or Green) depends on the team's assigned color.
	 * @return y-coordinate of the tunnel's upper right corner
	 */
	public int getTunnelURY() {
		int tunnelURY = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			tunnelURY = ((Long) data.get("TNR_UR_y")).intValue();
		}else {
			tunnelURY = ((Long) data.get("TNG_UR_y")).intValue();
		}
		return tunnelURY;
	}
	
	/**
	 * This method gets the x-coordinate of the search zone's lower left corner.
	 * The search zone (Red or Green) depends on the team's assigned color.
	 * @return x-coordinate of the search zone's lower left corner
	 */
	public int getSearchZoneLLX() {
		int searchZoneLLX = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			searchZoneLLX = ((Long) data.get("SZR_LL_x")).intValue();
		}else {
			searchZoneLLX = ((Long) data.get("SZG_LL_x")).intValue();
		}
		return searchZoneLLX;
	}
	
	/**
	 * This method gets the y-coordinate of the search zone's lower left corner.
	 * The search zone (Red or Green) depends on the team's assigned color.
	 * @return y-coordinate of the search zone's lower left corner
	 */
	public int getSearchZoneLLY() {
		int searchZoneLLY = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			searchZoneLLY = ((Long) data.get("SZR_LL_y")).intValue();
		}else {
			searchZoneLLY = ((Long) data.get("SZG_LL_y")).intValue();
		}
		return searchZoneLLY;
	}
	
	/**
	 * This method gets the x-coordinate of the search zone's upper right corner.
	 * The search zone (Red or Green) depends on the team's assigned color.
	 * @return x-coordinate of the search zone's upper right corner
	 */
	public int getSearchZoneURX() {
		int searchZoneURX = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			searchZoneURX = ((Long) data.get("SZR_UR_x")).intValue();
		}else {
			searchZoneURX = ((Long) data.get("SZG_UR_x")).intValue();
		}
		return searchZoneURX;
	}
	
	/**
	 * This method gets the y-coordinate of the search zone's upper right corner.
	 * The search zone (Red or Green) depends on the team's assigned color.
	 * @return y-coordinate of the search zone's upper right corner
	 */
	public int getSearchZoneURY() {
		int searchZoneURY = 0;
		if (getRedTeam() == TEAM_NUMBER) {
			searchZoneURY = ((Long) data.get("SZR_UR_y")).intValue();
		}else {
			searchZoneURY = ((Long) data.get("SZG_UR_y")).intValue();
		}
		return searchZoneURY;
	}
}
