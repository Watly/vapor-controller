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
 * All teh info useful to create DSL Sentences useful to Extract Drone Commands
 * @author Marco Vaspollo
 *
 */
public class Itinerary {
    
    private Coordinates start;
    private Coordinates end;
    private long maxHeight;
    private long weight;
    
    public Itinerary(Coordinates start, Coordinates end, long maxHeight, long weight) {
        super();
        this.start = start;
        this.end = end;
        this.maxHeight = maxHeight;
        this.weight = weight;
    }
    
    public Coordinates getStart() {
        return start;
    }
    
    public void setStart(Coordinates start) {
        this.start = start;
    }
    
    public Coordinates getEnd() {
        return end;
    }
    
    public void setEnd(Coordinates end) {
        this.end = end;
    }
    
    public long getMaxHeight() {
        return maxHeight;
    }
    
    public void setMaxHeight(long maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    public long getWeight() {
        return weight;
    }
    
    public void setWeight(long weight) {
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return "Itinerary [start=" + start + ", end=" + end + ", maxHeight=" + maxHeight + " cm, weight=" + weight + " hg]";
    }
}
