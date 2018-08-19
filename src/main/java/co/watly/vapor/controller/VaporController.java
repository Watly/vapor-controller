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

import static com.ea.async.Async.await;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import co.watly.Vapor;
import co.watly.vapor.data.DroneCommand;
import co.watly.vapor.data.Itinerary;
import co.watly.vapor.model.IDrone;
import co.watly.vapor.model.IDroneData;
import co.watly.vapor.util.DroneCommandMapper;
import co.watly.vapor.util.Helper;

/**
 * The Main Vapor Controller
 * A {@link IDrone} can be controlled in any way the user wants.
 * This class receives an itinerary, translate it into DSL 
 * directives and then convert it in Drone Commands through Vapor Generator
 * 
 * @author Marco Vasapollo
 *
 */
public class VaporController {
    
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    // The minimum speed percentage the Drone can reach with its load
    private static final double DEFAULT_MIN_DRONE_SPEED_PERCENTAGE = 0.3;
    
    // The maximum speed percentage the Drone can reach with its load, preserving the full speed for emergencies (e.g. wind)
    private static final double DEFAULT_MAX_DRONE_SPEED_PERCENTAGE = 0.7;
    
    // Percentage to raise the drone from the ground to carry out the maneuvers useful at the start
    private static final double FLIGHT_HEIGHT_DECRISING_PERCENTAGE = 0.3;
    
    private final IDrone drone;
    private IDroneData droneData;
    private DroneCommand[] droneCommands;
    
    //First thing the Controller does in constructor is to retrieve Drone data used for computation
    public VaporController(IDrone drone) {
        (this.drone = drone).getData().thenAcceptAsync(this::setDroneData);
    }
    
    public final IDrone getDrone() {
        return drone;
    }
    
    public final IDroneData getDroneData() {
        return droneData;
    }
    
    private final void setDroneData(IDroneData droneData) {
        this.droneData = droneData;
    }
    /**
     * The fly command. Can be called once drone data and commands are set
     */
    public final CompletableFuture<Void> fly() {
        if (droneData == null) {
            throw new RuntimeException("Please, wait for Drone data to arrive");
        }
        if (droneCommands == null || droneCommands.length == 0) {
            throw new RuntimeException("Please, set itinerary before to start flight");
        }
        System.out.println("Fly command fired, executing drone commands:\n\t" + Arrays.toString(droneCommands) + "\n");
        try {
            for (DroneCommand droneCommand : droneCommands) {
                await(DroneCommandMapper.invoke(droneCommand.getName(), droneCommand.getSpeed(), droneCommand.getArg(), drone));
            }
            System.out.println("All commands executed successfully! Drone landed to the end destination");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return completedFuture(null);
    }
    
    /**
     * Extract from all the maneuver commands sequence for the drone Through itinerary, 
     * converting it into DSL sentences 
     * @param itinerary Contains coordinates of Start and End and other properties useful for calculations
     */
    public final CompletableFuture<Void> setup(Itinerary itinerary) {
        System.out.println("New Itinerary to elaborate:\n\t" + itinerary + "\n");
        String dsl = await(CompletableFuture.supplyAsync(() -> convert(itinerary)));
        System.out.println("DSL generated from Itinerary:\n\t" + dsl + "\n");
        String dslOutput = await(Vapor.generate(dsl));
        System.out.println("Drone Commands generated from the DSL:\n\t" + dslOutput + "\n");
        droneCommands = DroneCommand.fromJSON(dslOutput);
        return completedFuture(null);
    }
    
    /**
     * Converts the given itinerary into DSL sentences
     * 
     * @param itinerary
     *            the given itineray
     * @return the itinerary, converted into DSL instance
     */
    private final String convert(Itinerary itinerary) {
        final StringBuilder dslBuilder = new StringBuilder();
        
        // DSL Header
        dslBuilder.append("scheduled vapor flight {").append("\n\t")
                // Mockup data
                .append(Helper.stringFormat("date: {0},", DATE_FORMAT.format(new Date(System.currentTimeMillis() + 10000000)))).append("\n\t").append(Helper.stringFormat("model: \"{0}\",", droneData.getModelName())).append("\n\t")
                // Mockup data
                .append(Helper.stringFormat("cost: {0} drops,", 100)).append("\n\t").append("commands: [").append("\n\t\t");
        
        // Useful to determine if the drone has the necessary autonomy to complete the itinerary
        long totalRoute = 0;
        
        // Min and Max drone speed
        long[] speeds = calculateSpeeds(itinerary.getWeight());
        
        // Flight height
        long flightHeight = calculateFlightHeight(itinerary.getStart().getHeight(), itinerary.getEnd().getHeight(), itinerary.getMaxHeight());
        
        System.out.println(Helper.stringFormat("Drone flight height will be {0} cm from the sea level", flightHeight));
        
        long currentStepDistance = flightHeight - itinerary.getStart().getHeight();
        
        totalRoute += currentStepDistance;
        
        long currentStepDuration = calculateDuration(speeds[0], currentStepDistance);
        
        // Get up from the ground
        dslBuilder.append(Helper.stringFormat("up at {0} cm/sec for {1} milliseconds", speeds[0], currentStepDuration));
        
        // Rotate to align - calculate the angle
        double angle = calculateAngle(itinerary.getStart().getLatitude(), itinerary.getEnd().getLatitude(), itinerary.getStart().getLongitude(), itinerary.getEnd().getLongitude());
        if(angle  > 0) {
            currentStepDistance = calculateRadialDistance(speeds[0], angle);
            totalRoute += currentStepDistance;
            
            dslBuilder.append(",\n\t\t").append(Helper.stringFormat("rotate at {0} cm/sec {1} degrees", speeds[0], angle));
        }
        // Go up to the flight space - keeping max flight
        
        // Go - at max speed
        currentStepDistance = calculateDistance(itinerary.getStart().getLatitude(), itinerary.getEnd().getLatitude(), itinerary.getStart().getLongitude(), itinerary.getEnd().getLongitude());
        totalRoute += currentStepDistance;
        currentStepDuration = calculateDuration(speeds[0], currentStepDistance);
        dslBuilder.append(",\n\t\t").append(Helper.stringFormat("forward at {0} cm/sec for {1} milliseconds", speeds[1], currentStepDuration));
        
        //Re-Align drone to north.
        if(angle  > 0) {
            angle = 360 - angle;
            currentStepDistance = calculateRadialDistance(speeds[0], angle);
            totalRoute += currentStepDistance;
            currentStepDuration = calculateDuration(speeds[0], currentStepDistance);
            
            dslBuilder.append(",\n\t\t").append(Helper.stringFormat("rotate at {0} cm/sec {1} degrees", speeds[0], angle));
        }
        // Land
        currentStepDistance = flightHeight - itinerary.getEnd().getHeight();
        
        totalRoute += currentStepDistance;
        
        currentStepDuration = calculateDuration(speeds[0], currentStepDistance);
        
        // Get up from the ground
        dslBuilder.append(",\n\t\t").append(Helper.stringFormat("down at {0} cm/sec for {1} milliseconds", speeds[0], currentStepDuration));
        
        System.out.println(Helper.stringFormat("Total itinerary is {0} cm", totalRoute));
        
        if (totalRoute > droneData.getTotalJourney()) {
            throw new InsufficientAutonomyException(droneData.getTotalJourney(), totalRoute);
        }
        
        // Finalize the DSL instance and return it
        return dslBuilder.append("\n\t").append("]").append("\n").append("}").toString();
    }
    
    /**
     * Calculates minimum and maximum speed the Drone can reach carrying current weight. It is derived from the max speed the drone can reach with full weight minimum speed is needed to position the drone in flight mode maximum speed is the cruise speed, gaining part of total speed for emergency cases (e.g. wind)
     * 
     * @param weight
     *            carried by the Drone (in hg)
     * @return position [0]: drone at minimum speed, position [1]: drone at maximum speed
     * @throws WeightExcessException
     *             if the weight loaded exceeds max weight supported by the drone
     */
    private final long[] calculateSpeeds(long weight) throws WeightExcessException {
        if (weight > droneData.getMaxWeightCapability()) {
            throw new WeightExcessException(weight, droneData.getMaxWeightCapability());
        }
        // First, we have full speed at max load capacity
        long speed = droneData.getMaxReachableSpeed();
        
        // Now we calculate the weight difference
        long weightDifference = droneData.getMaxWeightCapability() - weight;
        
        // Now we apply the conversion factor
        double conversionFactor = weightDifference * droneData.getSpeedDecreasingFactor();
        
        // Now calculate the 100% speed reachable with current weight
        // Decreasing because of stability
        speed = (long) ((double) speed - conversionFactor);
        
        long minSpeed = (long) ((double) speed * DEFAULT_MIN_DRONE_SPEED_PERCENTAGE);
        
        long maxSpeed = (long) ((double) speed * DEFAULT_MAX_DRONE_SPEED_PERCENTAGE);
        
        System.out.println(Helper.stringFormat("With a load of {0} hg, drone's min speed can be {1} cm/sec, while max speed can be {2} cm/sec", weight, minSpeed, maxSpeed));
        
        return new long[] { minSpeed, maxSpeed };
    }
    
    /**
     * Calculates flight height depending of max flight height Default maneuver height is the standard, but if it is too high, the maneuver height will be calculated starting from the max height of the itinerary
     * 
     * @param startHeight the elevation from the sea level of the Start Point
     * @param endHeight the elevation from the sea level of the Arrive Point
     * @param maxHeight the elevation from the sea level of the Flight Zone limit
     * @return the flight Height
     * t
     */
    private final long calculateFlightHeight(long startHeight, long endHeight, long maxHeight) {
        if(startHeight > maxHeight) {
            throw new FlightZoneLimitTrespassException(startHeight, true, maxHeight);
        }
        
        if(endHeight > maxHeight) {
            throw new FlightZoneLimitTrespassException(endHeight, false, maxHeight);
        }
        
        long flightHeight = maxHeight;
        flightHeight = (long) ((double) maxHeight * FLIGHT_HEIGHT_DECRISING_PERCENTAGE);
        if(flightHeight <= startHeight || flightHeight <= endHeight) {
            flightHeight = maxHeight;
        }
        return flightHeight;
    }
    
    /**
     * TODO Marco Vasapollo 2017/04/9 - WRONG CODE!!!!! CHANGE IMMEDIATELY!!! 
     * 
     * Calculates the amount of distance traveled based on speed and rotation
     * 
     * @param speed
     *            expressed in cm/sec
     * @param degrees
     *            expressed in grades
     * @return the amount of distance traveled at given speed while rotating
     */
    private final long calculateRadialDistance(long speed, double degrees) {
        return (long) Math.ceil((double) speed * Math.abs(degrees) / 1000.0);
    }
    
    /**
     * Calculates the duration useful to cover the given distance given at the current speed
     * 
     * @param speed
     *            expressed in cm/sec
     * @param distance
     *            expressed in cm
     * @return the duration, expressed in milliseconds
     */
    private final long calculateDuration(long speed, long distance) {
        return (long) (Math.abs((double) distance) / (double) speed * 1000.0);
    }
    
    /**
     * Calculates the distance between two coordinates
     * @param lat1 Start Latitude
     * @param lat2 End Latitude
     * @param lon1 Start Longitude
     * @param lon2 End Longitude
     * @return distance between coordinates in cm
     */
    private static final long calculateDistance(double lat1, double lat2, double lon1, double lon2) {
        
        final int R = 6371; // Radius of the earth
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 100000;
        return (long) distance;
    }
    
    /**
     * Calculates the rotation angle between two coordinates
     * @param lat1 Start Latitude
     * @param lat2 End Latitude
     * @param lon1 Start Longitude
     * @param lon2 End Longitude
     * @return angle between coordinates in degrees
     */
    private static final double calculateAngle(double lat1, double lat2, double lon1, double lon2)
    {
        double angle = Math.toDegrees(Math.atan2(lat2 - lat1, lon2 - lon1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil( -angle / 360 ) * 360;

        return angle;
    }
}
