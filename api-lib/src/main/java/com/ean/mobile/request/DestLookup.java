/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.ean.mobile.Constants;
import com.ean.mobile.exception.JsonParsingException;

/**
 * Looks up possible destinations based on the destinationString passed to getDestInfos.
 */
public final class DestLookup {
    private static final String ENDPOINT_FORMAT = "http://api.ean.com/ean-services/lookup/?propertyName=%s";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private DestLookup() {
        //see javadoc.
    }

    /**
     * Looks up possible destinations using the destinationString. Will return an array of suggestions.
     * @param destinationString The string passed in by the user.
     * @return The array of possible destinations meant by the destinationString
     * @throws IOException If there is a problem communicating on the network.
     */
    public static JSONArray getDestInfos(final String destinationString) throws IOException {
        if ("".equals(destinationString) || destinationString == null) {
            return new JSONArray();
        }
        final String baseUrl = String.format(ENDPOINT_FORMAT, destinationString);
        Log.d(Constants.DEBUG_TAG, baseUrl);
        Log.d(Constants.DEBUG_TAG, "getting response");
        final JSONObject json;
        final HttpResponse response
            = new DefaultHttpClient().execute(new HttpGet(baseUrl));
        Log.d(Constants.DEBUG_TAG, "got response");
        final StatusLine statusLine = response.getStatusLine();
        try {
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                final String jsonstr = out.toString();
                Log.d(Constants.DEBUG_TAG, jsonstr);
                json = new JSONObject(jsonstr);
            } else {
                // If we didn't get a good http status, then the connection failed.
                // Close the connection and throw an error.
                Log.d(Constants.DEBUG_TAG, "Connection Status not ok: " + statusLine.getStatusCode());
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
            return json.getJSONArray("items");
        } catch (JSONException jse) {
            throw new JsonParsingException(jse);
        }
    }
}
