package ca.mcgill.ecse211.model;

import lejos.robotics.SampleProvider;

/**
 * This class implements color classification to identify the color of the cans. Essentially, it
 * implements the color sensor to obtain the RGB values of the colored cans. Then, the RGB values
 * collected will be normalized by the average over all 3 channels, and compared with the normalized
 * mean value of the color of each can. The color can be identified once the minimum difference in
 * the RGB values is met.
 * 
 * @author Hao Shu
 * @author Carlo D'Angelo
 * @author Ketan Rampurkar
 * @author Mohamed Samee
 * @author Marie-Lynn Mansour
 * @author Cristian Ciungu
 * @version 25-02-2019
 */
public class ColorClassification {

  // use an array to collect color data
  private float[] colorData;
  private SampleProvider colorID;
  private static final double THRESHOLD = 0.35;

  /**
   * This 2d array stores the mean RGB values for each colored can.
   * 
   */
  private final float[][] MEAN_RGB = {// the mean RGB values for cans
	      {0.0551924454f, 0.0069923890f, 0.0339901908f}, // red can
	      {0.0520519090f, 0.0347073221f, 0.0072014234f}, // yellow can
	      {0.0107042342f, 0.0406447724f, 0.0345459889f}, // blue can
	      {0.0235257231f, 0.0502034322f, 0.0178382312f}	 // green can
	  };

  /**
   * Constructor to initialize variables.
   * 
   * @param float[] colorData
   * @param SampleProvider colorId
   */
  public ColorClassification(float[] colorData, SampleProvider colorId) {
    this.colorData = colorData;
    this.colorID = colorId;

  }

  /**
   * This method receives the position of the detected color and returns the name of the color as a
   * String.
   * 
   * @return String
   */
  public String run() {
    int a = findColor(sampleData());

    if (a !=4) {
      String[] clrName = {"red      ", "yellow   ", "blue     ", "green    "};
      return clrName[a];
    } else {
      return "no object";
    }


  }

  /**
   * This method normalizes the RGB data collected by the color sensor, and then compare them with
   * the normalized mean RGB values one by one. If the minimum difference is met, the position of
   * the detected color in the 2d array will be returned as an int.
   * 
   * @param colorData
   * @return i : int
   */
  private int findColor(float[] colorData) {
    float eucDistance = (float) Math.sqrt((Math.pow(colorData[0], 2) + Math.pow(colorData[1], 2) + Math.pow(colorData[2], 2)));

    // normalize R,G,B values
    float nR = colorData[0] / eucDistance;
    float nG = colorData[1] / eucDistance;
    float nB = colorData[2] / eucDistance;
    // use a counter and difference in RGB values to classify the color
    for (int i = 0; i < 4; i++) {
      float deltaR = Math.abs(nR - (MEAN_RGB[i][0]/eucDistance));
      float deltaG = Math.abs(nG - (MEAN_RGB[i][1]/eucDistance));
      float deltaB = Math.abs(nB - (MEAN_RGB[i][2]/eucDistance));
 
      if (deltaR < THRESHOLD && deltaG < THRESHOLD && deltaB < THRESHOLD) {
        
        return i;
      }

    }

    return 4;
  }
  
  /**
   * This method is used to fetch the RGB values from the color sensor and store them in an array.
   * 
   * @return colorData : float[]
   */
  private float[] sampleData(){
  colorID.fetchSample(colorData, 0);
  return colorData;
  }

}
