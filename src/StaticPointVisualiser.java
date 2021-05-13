package eot_Schlagbauer_Salvans_Servais_Rossboth;

//used JARs
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/* The CLASS 'StaticPointVisualiser' is created for the purpose of visualizing the physioMeasurements- and the tweets-data 
 * in a static way by using the processing library.
 * 
 */

public class StaticPointVisualiser extends PApplet {
	
	// get the content for visualisation
	
	// map	
	WMSConnector wmsconnector = new WMSConnector();
	PImage wms_image = wmsconnector.getwms(); // returns wms_image
		
	// physioMeasurements
	WFSConnector wfs = new WFSConnector();
	ArrayList<Double> wfsX = wfs.getX(); // returns coordinates
	ArrayList<Double> wfsY = wfs.getY(); // returns coordinates
	ArrayList<Integer> wfsHeartrate = wfs.getHeartrate(); //returns heart-rate data

	// tweets
	PGConnector pg = new PGConnector();
	ArrayList<String>pgemo = pg.getEmotion(); // returns emotions
	ArrayList<Double> pgX = pg.getX(); // returns coordinates
	ArrayList<Double> pgY = pg.getY(); // returns coordinates
	
	//------------------------------------------------
	
	public static void main(String[] args) {
		// The argument passed to main must match the fully qualified class name
		PApplet.main("eot_Schlagbauer_Salvans_Servais_Rossboth.StaticPointVisualiser");
	}

	// method used only for setting the size of the window
	public void settings() {
		size(1000, 1000);
	}

	public void setup() {
		background(0);
	}

	public void draw() {

		image(wms_image, 0, 0);

		// VISUALISATION for WFSConnector
		for (int i = 0; i < wfsX.size(); i++) {

			double printXtemp = wfsX.get(i);
			// Precision loss in double to float!!!!,
			// but float is needed for printing of ellipse
			float printX = (float) printXtemp;

			double printYtemp = wfsY.get(i);
			// Precision loss in double to float!!!!,
			// but float is needed for printing of ellipse
			float printY = (float) printYtemp;

			int printHeart = wfsHeartrate.get(i);
			   if (printHeart < 120) fill(1, 121, 28); // dark green
			   else if (printHeart >= 120 && printHeart < 130) fill(104, 255, 5); // light green
			   else if (printHeart >= 130 && printHeart < 140) fill(255, 126, 5); // orange
			   else if (printHeart >= 140 && printHeart < 150) fill(232, 44, 44); // light red
			   else if (printHeart >= 150) fill(255, 0, 0); // dark red
			   else fill (255);
			noStroke();
			// ellipse size is related to the heart-rate value
			ellipse(printX, printY, ((printHeart/10)-5), ((printHeart/10)-5)); 
		}

		// VISUALISATION for PGConnector
		for (int i = 0; i < pgX.size(); i++) {

			double printXtemp = pgX.get(i);
			// Precision loss in double to float!!!!,
			// but float is needed for printing of ellipse
			float printX = (float) printXtemp;

			double printYtemp = pgY.get(i);
			// Precision loss in double to float!!!!,
			// but float is needed for printing of ellipse
			float printY = (float) printYtemp;
			fill(255, 0, 0);
			
			if(pgemo.get(i).contentEquals("happiness")) fill(0,255,0);  // green
			if(pgemo.get(i).contentEquals("anger_disgust")) fill(255,0,0);  // red
			if(pgemo.get(i).contentEquals("sadness")) fill(0,0,255);  // blue
			if(pgemo.get(i).contentEquals("fear")) fill(125);  // grey
			
			stroke(5);
			ellipse(printX, printY, 10, 10);

		}
		
	}
	
} // End CLASS 'StaticPointVisualizer'