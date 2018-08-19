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

import com.google.gson.Gson;

/**
 * Utility class to Serialize/Deserialize JSONs.
 * It masks the use of the real JSON engine provider 
 * @author Marco Vasapollo
 *
 */
public final class JSON {
    
    private static final Gson GSON = new Gson();

    public static final String convert(Object o) {
        return GSON.toJson(o);
    }
    
    public static final <T> T parse(String json, Class<? extends T> type) {
        return GSON.fromJson(json, type);
    }
}
