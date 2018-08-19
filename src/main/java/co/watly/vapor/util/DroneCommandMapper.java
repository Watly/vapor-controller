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
package co.watly.vapor.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import co.watly.vapor.model.IDrone;

/**
 * Utility class that map each {@link DroneCommand} into {@link IDrone} methods.
 * This avoids a lot of headaches in synchronizing Drone Model and DSL Grammar updates with ugly and boring if/else sentences
 * @author Marco Vasapollo
 *
 */
public class DroneCommandMapper {
    
    private static final Map<String, Method> COMMANDS = new HashMap<>();
    private static final Map<String, Class<?>> SECOND_PARAMS = new HashMap<>();
    
    static {
        try {
            Method[] methods = IDrone.class.getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();
                COMMANDS.put(name, method);
                try {
                    SECOND_PARAMS.put(name, method.getParameterTypes()[1]);
                }
                catch(Exception e) {   
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static final CompletableFuture<Void> invoke(String command, long speed, Number secondParameter, IDrone drone) {
        try {
            return (CompletableFuture<Void>) COMMANDS.get(command).invoke(drone, speed, Number.class.getMethod(SECOND_PARAMS.get(command).getSimpleName().toLowerCase() + "Value").invoke(secondParameter));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}