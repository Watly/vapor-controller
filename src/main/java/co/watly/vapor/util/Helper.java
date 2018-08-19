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

import java.io.File;
import java.nio.file.Files;

/**
 * Generic Utility Methods
 * @author Marco Vasapollo
 *
 */
public final class Helper {

    /*
     * Java implementation of Microsoft .NET's string.IsNullOrWhiteSpace(string test) native method
     */
    public static final boolean stringIsNullOrWhiteSpace(String string) {
        return string == null || string.trim().isEmpty();
    }

    /*
     * Java implementation of Microsoft .NET's string.Format(string template, Object[] args) native method
     */
    public static final String stringFormat(String template, Object firstValue, Object... otherValues) {
        if (stringIsNullOrWhiteSpace(template)) {
            return template;
        }
        String firstVal = firstValue == null ? "null" : firstValue.toString();
        String f = template.replace("{0}", firstVal);
        if (otherValues != null && otherValues.length > 0) {
            for (int i = 0; i < otherValues.length; i++) {
                String n = "{" + (i + 1) + "}";
                String v = otherValues[i] == null ? "null" : otherValues[i].toString();
                f = f.replace(n, v);
            }
        }
        return f;
    }
    
    /*
     * Exception-aware Utility method to read text from a file using its path
     */
    public static final String readTextFile(String fileName) {
        try {
            return new String(Files.readAllBytes(new File(fileName).toPath()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Exception-aware Utility method to convert a JSON file to its related Java P.O.J.O.
     */
    public static final <T> T readJSONFile(String fileName, Class<? extends T> type) {
        return JSON.parse(readTextFile(fileName), type);
    }
}
