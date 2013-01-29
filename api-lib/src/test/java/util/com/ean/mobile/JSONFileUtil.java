/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
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
