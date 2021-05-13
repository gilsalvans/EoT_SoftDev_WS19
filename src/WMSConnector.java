package eot_Schlagbauer_Salvans_Servais_Rossboth;

//used JARs
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.request.GetMapRequest;
import org.geotools.ows.wms.response.GetMapResponse;
import processing.core.PImage;

/* The CLASS 'WMSConnector' is created for the purpose of connecting to the WebMapServer.
 * 
 * 'WMSConnector' is a non-executable class. 
 * It has the method:  getBoundaries()     		returns the array with x-/y- min/max (map-boundaries)
 * 					   getwms()					retrieves the WMS output 
 * 
 */

public class WMSConnector {

	PImage wms_image; // define the image of the WMS as a global variable

	// bounding box for our study area
	double X_min = -71.03;
	double X_max = -71.13;
	double Y_min = 42.32;
	double Y_max = 42.42;

	/* storing the bounding box values into an array, so we can call it in each
	 class instead of inserting the values manually */
	Double[] arr = new Double[] { X_min, X_max, Y_min, Y_max };

	public WMSConnector() {

		URL url = null;
		try {
			url = new URL("http://maps.heigit.org/osm-wms/service?service=WMS&request=GetCapabilities&version=1.1.1"); //WMS url
		} catch (MalformedURLException e) {
		}

		WebMapServer wms = null;
		try {
			wms = new WebMapServer(url);  //using the defined url to create a WMS object, so we can access the service programatically
		} catch (IOException e) {
		} catch (ServiceException e) {
		}
		// Creating a request for the specified web map service
		GetMapRequest request = (GetMapRequest) wms.createGetMapRequest();

		// Setting the parameters for our request
		request.setSRS("EPSG:4326");
		request.setBBox(X_max + "," + Y_min + "," + X_min + "," + Y_max); // using the previously defined array for the BBox
		request.setDimensions("1000", "1000");
		request.setFormat("image/png");
		request.setTransparent(true);
		request.setVersion("1.1.1");
		request.addLayer("osm_auto:all", "default");

		GetMapResponse response = null;

		try {
			response = (GetMapResponse) wms.issueRequest(request);
		} catch (ServiceException | IOException e) {
			e.printStackTrace();
		}
		try {    
			BufferedImage image = ImageIO.read(response.getInputStream()); // Creating a buffered image object to read the WMS response output
			wms_image = new PImage(image); // Storing the output (base map) as a PImage for the later visualization with Processing
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PImage getwms() { //method which will be used in the visualizer classes to retrieve the WMS map
		return wms_image;
	}

	public Double[] getBoundaries() { //method to get the boundaries defined in the array for the other classes
		return arr;
	}

} // End CLASS 'WMSConnector'