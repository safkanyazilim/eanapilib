/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
 */

package com.ean.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Utility class containing methods for extracting JSON data from external files.
 */
public final class JSONFileUtil {

    /**
     * Private, no-op constructor to prevent utility class instantiation.
     */
    private JSONFileUtil() {
        // see javadoc
    }

    /**
     * Uses the provided fileName to load a JSON file and returns the contents as a JSONObject.
     * @param fileName the file name of the desired file. The file MUST be located in the com.ean.mobile package.
     * @return a JSONObject containing the data from the file.
     */
    public static JSONObject loadJsonFromFile(final String fileName) {
        try {
            return new JSONObject(loadJsonStringFromFile(fileName));
        } catch (JSONException jsone) {
            Log.e(Constants.LOG_TAG, String.format("Unable to parse JSON from %s.", fileName), jsone);
            return null;
        }
    }

    private static String loadJsonStringFromFile(final String fileName) {
        InputStream jsonResource = JSONFileUtil.class.getResourceAsStream(fileName);
        if (jsonResource == null) {
            throw new RuntimeException(
                String.format("Could not load %s. Check classpath and resource settings.", fileName));
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(jsonResource));
        final StringBuilder jsonBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } catch (IOException ioe) {
            Log.e(Constants.LOG_TAG, String.format("Unable to load data from %s.", fileName), ioe);
            jsonBuilder.append("error");
        }
        return jsonBuilder.toString();
    }

}
