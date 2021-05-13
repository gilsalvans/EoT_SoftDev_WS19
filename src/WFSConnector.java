package eot_Schlagbauer_Salvans_Servais_Rossboth;

//used JARs
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

/* The CLASS 'WFSConnector' is created for the purpose of connecting to the physioMeasurements-data.
 * 
 * 'WFSConnector' is a non-executable class. 
 * It has the methods:  getX()     		returns the arraylist with x-coordinates
 * 						getY()			returns the arraylist with y-coordinates
 * 						getAttribute() 	returns the arraylist with the time/date
 * 						getHeartrate() 	returns the arraylist with the heartrate-data
 * 
 * We already implement the coordinates transformation for the visualisation within this class too.
 */

public class WFSConnector {
	
	//defining the Array Lists as a global variable to access them throughout the methods
	public static ArrayList<Double> arrX = new ArrayList<Double>();
	public static ArrayList<Double> arrY = new ArrayList<Double>();
	public static ArrayList<Integer> arrHeart = new ArrayList<Integer>();
	public static ArrayList<LocalTime> arrTime = new ArrayList<LocalTime> ();
	
	/*In order to get the boundaries for the conversion of the WFS coordinates, we call up the WFS Connector and its method getBoundaries.
	* This way we only define the variable once and can still access them in the WFSConnector and PGConnector class.
	*/
	WMSConnector bound = new WMSConnector();
	Double[] arrBound = bound.getBoundaries();

	
	public WFSConnector() {
		//variables for coordinates conversion according to the maps-frame
		double X_min = (double) arrBound[0];
		double X_max = (double) arrBound[1];
		double Y_min = (double) arrBound[2];
		double Y_max = (double) arrBound[3];
		double X_pixel_max = 1000.00;
		double Y_pixel_max = 1000.00;

		String url = "http://zgis221.geo.sbg.ac.at/geoserver/lv856152_153/ows/?service=wfs&version=1.1.0&request=GetCapabilities";
		//String url = "http://47.91.72.131:8080/geoserver/jerry/ows/?service=wfs&version=1.1.0&request=GetCapabilities";
		
		HashMap connectionParameters = new HashMap();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", url);
		WFSDataStoreFactory dsf = new WFSDataStoreFactory();

		try {
			WFSDataStore dataStore = dsf.createDataStore(connectionParameters);
			//accessing the specific Feature type 
			SimpleFeatureSource source = dataStore.getFeatureSource("lv856152_153:physioMeasurements03");
			//SimpleFeatureSource source = dataStore.getFeatureSource("jerry:physioMeasurements03");
			SimpleFeatureCollection fc = source.getFeatures();
			SimpleFeatureIterator wfsFeatures = fc.features();

			//Iteration through the individual events in the WFS to store them in the designated Array List
			while (wfsFeatures.hasNext()) {
				SimpleFeature sf = wfsFeatures.next();
				/* The coordinates of the events are not stored separately in individual columns but in the geometry.  
				 * We therefore have to first store the geometry of the event in order to access the x- and y-coordinates. 
				 * Methods for the further handling of the class Geometry are outlined on the web side of the library.
				 * Link: https://locationtech.github.io/jts/javadoc/org/locationtech/jts/geom/Geometry.html */
				
				Geometry geom = (Geometry) sf.getDefaultGeometry();
				//the method getCoordinate returns the vertexes of the object
				Coordinate coord = geom.getCoordinate();

				/* Apparently the coordinates of WFS have been swapped within the geometry
				 * Therefore, we had to re-swap them in the storage here. */
				double xValue = coord.getY();// !!!! X <- Y
				double yValue = coord.getX();

				// Coordinate Converter
				double xConverted = (((X_max - xValue) / (X_max - X_min)) * X_pixel_max);
				double yConverted = (((Y_max - yValue) / (Y_max - Y_min)) * Y_pixel_max);

				/* Heartrate Parser
				 * The heartrate can not be stored as a integer just as a string. 
				 * Therefore, we convert the heartrate into integer values in the second line of code. */
				Object heartTemp = sf.getAttribute("heartrate");
				int heart = Integer.valueOf(String.valueOf(heartTemp));
				
				/* Time Parser
				 * Similar to the heartrate we can only retrieve the time and date stored together as a string.
				 * In order to work with the time and date we have to convert them into the data types LocalDateTime and LocalTime. 
				 * The Date and Time are stored first have to be accessed which are stored under the attribute "phenomenon". 
				 * Methods for the further handling of the class Geometry are outlined on the web side of the library.
				 * Link: https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html */
				String date_time =(String) sf.getAttribute("phenomenon");
				try {
					/* The data is already stored according to ISO Standards ("yyyy-MM-dd'T'HH:mm:ss").
					 * This allows us to use the ISO Formatting for directly parse date and time. 
					 * For our time series visualiser the date of the events is not important therefore we only store the time as 
					 * LocalTime. */
					DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
					LocalDateTime dateTime = LocalDateTime.parse(date_time,formatter);
					//By using the LocalDateTime data type we can easily access the time alone with predefined methods.
					LocalTime time = dateTime.toLocalTime();
					arrTime.add(time);
								
				}catch (Exception e) {
					e.printStackTrace();
				}

				//Each event is added to the global array lists which can be accessed thanks to the following return methods
				arrX.add(xConverted);
				arrY.add(yConverted);
				arrHeart.add(heart);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	//methods to get requested array lists containing x-, y-coordinate, heartrate and time 
	public ArrayList<Double> getX() {
		return arrX;
	}

	public ArrayList<Double> getY() {
		return arrY;
	}

	public ArrayList<Integer> getHeartrate() {
		return arrHeart;
	}
	public ArrayList<LocalTime> getTime() {
		return arrTime;
	}
}