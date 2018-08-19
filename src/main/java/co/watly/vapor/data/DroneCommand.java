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

import co.watly.vapor.util.JSON;

/**
 * DSL Generator converts its sentences into these commands
 * @author Marco Vasapollo
 *
 */
public class DroneCommand {
    
    private String name;
    private long speed;
    private Number arg;
    
    public DroneCommand() {
        super();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getSpeed() {
        return speed;
    }
    
    public void setSpeed(long speed) {
        this.speed = speed;
    }
    
    public Number getArg() {
        return arg;
    }
    
    public void setArg(Number arg) {
        this.arg = arg;
    }
    
    @Override
    public String toString() {
        return "DroneCommand [name=" + name + ", speed=" + speed + ", arg=" + arg + "]";
    }
    
    public static final DroneCommand[] fromJSON(String json) {
        return JSON.parse(json, DroneCommand[].class);
    }
}
