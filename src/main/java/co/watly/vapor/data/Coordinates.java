/**
 * This work is licensed under the 
 * Creative Commons Attribution-ShareAlike 4.0 International License. 
 * To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/4.0/ 
 * or send a letter to 
 * Creative Commons, 
 * PO Box 1866, Mountain View, 
 * CA 94042, USA.
 */
package co.watly.vapor.data;

/**
 * Latitude, Longitude and Elevation from the Sea Level of a certain Point
 * 
 * @author Marco Vasapollo
 *
 */
public class Coordinates {
    
    private double latitude;
    private double longitude;
    private long height;
    
    public Coordinates(double latitude, double longitude, long height) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public long getHeight() {
        return height;
    }
    
    public void setHeight(long height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Coordinates [latitude=" + latitude + ", longitude=" + longitude + ", height=" + height + " cm]";
    }
}