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

/**
 * Thrown if the current Drone Weight is not compatiple with the max Drone weight support 
 * @author Marco Vasapollo
 *
 */
public class WeightExcessException extends RuntimeException {
    
    private static final long serialVersionUID = -986930759395403886L;

    public WeightExcessException(long weight, long maxWeight) {
        super("Weight of " + weight + " hg exceedes max weight drone capacity of " + maxWeight + " hg");
    }
}
