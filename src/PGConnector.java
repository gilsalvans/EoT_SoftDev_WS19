package eot_Schlagbauer_Salvans_Servais_Rossboth;

//used jars
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

/* The CLASS 'PGConnector' is created for the purpose of connecting to the tweets-data.
 * 
 * 'PGConnector' is a non-executable class. 
 * It has the methods:  getX()     		returns the array with x-coordinates
 * 						getY()     		returns the array with y-coordinates
 * 						getEmotion ()  	returns the array with emotions
 * 
 * We already implement the coordinates transformation for the visualisation within this class too.
 */

public class PGConnector {
	//defining the Array Lists as a global variable to accesse them throughout the methods
	public static ArrayList<Double> arrX = new ArrayList<Double>();
	public static ArrayList<Double> arrY = new ArrayList<Double>();
	public static ArrayList<String> arrEMO = new ArrayList<String>();
	
	/*In order to get the boundaries for the conversion of the WFS coordinates, we call up the WFS Connector and its method getBoundaries.
	* This way we only define the variable once and can still access them in the WFSConnector and PGConnector class.
	*/
	WMSConnector bound = new WMSConnector();
	Double[] arrBound = bound.getBoundaries();
	
	public PGConnector() {
		
		// variables for connecting to the PostGIS

		// ZGIS Database
		String url = "jdbc:postgresql://zgis221.geo.sbg.ac.at/lv856152_153";
		String username = "sgroup01";
		String pw = "salzach2017$";
		
		//Boquin DB
		//String url = "jdbc:postgresql://47.91.72.131:5432/swd";
		//String username = "agi";
		//String pw = "salzach2020$";
		
		
		try {
			Connection conn = DriverManager.getConnection(url, username, pw);
			
			// Prefiltered SQL-Statement to access the data
			// ZGIS Database
			String query = "SELECT ST_Y(ST_AsText(\"geom\")) as \"lat\", ST_X(ST_AsText(\"geom\")) as \"long\", emotion FROM jdbc_boston_twitter;";
						
			//Boquin query
			// String query = "SELECT ST_Y(ST_AsText(\"geom\")) as \"lat\", ST_X(ST_AsText(\"geom\")) as \"long\", \"jEmotion\", txt FROM twitter;";
	   		 
			//explain PG Connector
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);

			//variables for coordinates conversion according to the maps-frame
			double Y_min = (double) arrBound[3];
			double Y_max = (double) arrBound[2];
			double X_max= (double) arrBound[0];
			double X_min = (double) arrBound[1];  
			
			//settings for visualization ==> size of the screen and wms image
			int X_pixel_max = 1000;
			int Y_pixel_max = 1000;

			//Iteration through the table columns
			while (rset.next()) {
				//get coordinates
				double lat = rset.getDouble("lat");
				double lon = rset.getDouble("long");
				
			
				//getemotion
				String emotion = rset.getString("emotion");
				//String emotion = rset.getString("jEmotion");	//boqins attribute name
				
				
				//Coordinate Converter
				double xConverted = (((lon - X_min) / (X_max - X_min)) * X_pixel_max);
				double yConverted = (((lat - Y_min) / (Y_max - Y_min)) * Y_pixel_max);

				// Not all of the features are in the bbox of the tile (WMS) 
				//-> Spatial filter
				if (lat <= Y_min && lat >= Y_max && lon <= X_max && lon >= X_min) {
					
					// Each event is added to the global array lists which can be accessed thanks to the following methods
					arrX.add(xConverted);
					arrY.add(yConverted);
					arrEMO.add(emotion);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	//methods to get requested array lists containing x-, y-coordinate and emotion
	public ArrayList<Double> getX() {
		return arrX;
	}
	public ArrayList<Double> getY() {
		return arrY;
	}
	public ArrayList<String> getEmotion() {
		return arrEMO;
	}
}