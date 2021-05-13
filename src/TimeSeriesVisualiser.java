package eot_Schlagbauer_Salvans_Servais_Rossboth;

//Used JARs
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/* The CLASS 'TimeSeriesVisaliser' is created for the purpose of visualizing the WMS as well as the tweet data from PGConnector 
 * as well as the physioMeasurements-data from the WFSConnector by using the processing library. 
 */

public class TimeSeriesVisualiser extends PApplet {

	// get the content for visualization

	// WMS as map
	WMSConnector wmsconnector = new WMSConnector();
	PImage wms_image = wmsconnector.getwms();

	// physioMeasurements
	WFSConnector wfs = new WFSConnector();
	ArrayList<Double> wfsX = wfs.getX();
	ArrayList<Double> wfsY = wfs.getY();
	ArrayList<LocalTime> wfsTime = wfs.getTime();
	ArrayList<Integer> wfsHeartrate = wfs.getHeartrate();
	
	// tweets
	PGConnector pg = new PGConnector();
	ArrayList<String> pgemo = pg.getEmotion();
	ArrayList<Double> pgX = pg.getX();
	ArrayList<Double> pgY = pg.getY();
	

	public static void main(String[] args) {
		// The argument passed to main must match the fully qualified class name
		PApplet.main("eot_Schlagbauer_Salvans_Servais_Rossboth.TimeSeriesVisualiser");
	}

	/* variables for iteration:
	 * i for physioMeasurements 
	 * j for tweets
	 * By using two variables we can vary the cycle within the iteration of values
	 */
	int i = 0;
	int j = 0;

	// method used only for setting the size of the window
	public void settings() {
		size(1000, 1000);
	}

	public void setup() {
		stroke(0); // Set line drawing color to black
		frameRate(100); // Set frame rate for visualization
		background(0); // Set the background to black
		// We draw the WMS image in the setup so that the tweets and physioMeasurements
		// are displayed upon it and not deleted.
		image(wms_image, 0, 0);
	}

	public void draw() { // it acts like a loop
		
		noStroke();
		
		//Visualization of physioMeasurements from the class WFSConnector
		//In order to display these points we need to convert them from double to float values. 
		double printXtemp = wfsX.get(i);
		float printX = (float) printXtemp;
		double printYtemp = wfsY.get(i);
		float printY = (float) printYtemp;
		
		/* Time Formatter
		 * The data type LocalTime needs to be converted to a string and formatted in order to display the values. 
		 */
		LocalTime wfstime = wfsTime.get(i);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String timeString = wfstime.format(formatter);
		
		//Visualization of tweets from the class PGConnector
		//In order to display these points we need to convert them from double to float values.
		double printXpg = pgX.get(j);
		float printx = (float) printXpg;
		double printYpg = pgY.get(j);
		float printy = (float) printYpg;
		
		//If loop for the visualization of physioMeasurement data depending on 5 classes of heartrates
		int printHeart = wfsHeartrate.get(i);
		if (printHeart < 120)
			fill(1, 121, 28);
		else if (printHeart >= 120 && printHeart < 130)
			fill(104, 255, 5);
		else if (printHeart >= 130 && printHeart < 140)
			fill(255, 126, 5);
		else if (printHeart >= 140 && printHeart < 150)
			fill(232, 44, 44);
		else if (printHeart >= 150)
			fill(255, 0, 0);
		else
			fill(255);
		//the size of the points on the map are also dependend on the heartrate 
		ellipse(printX, printY, ((printHeart / 10) - 5), ((printHeart / 10) - 5));
		
		//If loop for the visualization of tweet data depending on the emotion assigned to them
		if (pgemo.get(j).contentEquals("happiness"))
			fill(0, 255, 0);
		if (pgemo.get(j).contentEquals("anger_disgust"))
			fill(255, 0, 0);
		if (pgemo.get(j).contentEquals("sadness"))
			fill(0, 0, 255);
		if (pgemo.get(j).contentEquals("fear"))
			fill(125);

		stroke(5);
		ellipse(printx, printy, 15, 15);
		
		/*Loops for the visualization of the data
		 * If loop for the physioMeasurement data 
		 * The time difference between each event is more or less 1-2 s 
		 * Therefore, the variable i increases per Frame by one.*/
		if (i < wfsHeartrate.size() - 1)
			i++;
		/* If loop for the tweet data
		 * The time difference within the tweet data is bigger, therefore we imitate the delay within the twitter data by 
		 * manipulating the j variable. The j variable increases just every 20 iterations of the i variable. */
		if (i % 20 == 0)
			j++;
		// If loop to stop the drawing of tweet data
		if (j == pgX.size())
			j = 1;

		
		// Defining text specifying time (day/hours) of the data and a rectangle as background for the text
		fill(255); 
		rect(20, 20, 100, 30);
		textSize(16);
		fill(0);
		text(timeString, 30, 40);
	}

}