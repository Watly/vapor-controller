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
 * Thrown if Start or End point elevation terspass the Flight Zone
 * @author Marco Vasapollo
 *
 */
public class FlightZoneLimitTrespassException extends RuntimeException {

    private static final long serialVersionUID = -7781341412717663648L;

    public FlightZoneLimitTrespassException(long height, boolean start, long maxHeight) {
        super(Helper.stringFormat("The {0} point elevation of {1} cm exceedes the flight zone limit of {2} cm", start ? "Start" : "End", height, maxHeight));
    }
}
