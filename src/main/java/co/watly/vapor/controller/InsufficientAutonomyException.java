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
package co.watly.vapor.controller;

import co.watly.vapor.util.Helper;

/**
 * Thrown if the entire Journey Exceedes max Drone Autonomy
 * @author Marco Vasapollo
 *
 */
public class InsufficientAutonomyException extends RuntimeException {
    
    private static final long serialVersionUID = 7802861531517630575L;

    public InsufficientAutonomyException(long maxAutonomy, long itinerary) {
        super(Helper.stringFormat("The total drone autonomy ({0} cm) is insufficient to complete the itinerary of {1} cm", maxAutonomy, itinerary));
    }
}
