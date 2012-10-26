/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import android.util.Log;
import com.ean.mobile.EANMobileConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DestLookup {
    private static final String ENDPOINT_FORMAT = "http://api.ean.com/ean-services/lookup/?propertyName=%s";

    public static JSONArray getDestInfos(String destinationString) throws IOException, JSONException {
        if ("".equals(destinationString) || destinationString == null) {
            return new JSONArray();
        }
        String baseUrl = String.format(ENDPOINT_FORMAT, destinationString);
        Log.d(EANMobileConstants.DEBUG_TAG, baseUrl);
        Log.d(EANMobileConstants.DEBUG_TAG,"getting response");
        JSONObject json;
        HttpResponse response
            = (new DefaultHttpClient())
                .execute(new HttpGet(baseUrl));
        Log.d(EANMobileConstants.DEBUG_TAG,"got response");
        StatusLine statusLine = response.getStatusLine();
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            String jsonstr = out.toString();
            Log.d(EANMobileConstants.DEBUG_TAG, jsonstr);
            json = new JSONObject(jsonstr);
        } else{
           // Closes the connection.
            Log.d(EANMobileConstants.DEBUG_TAG, "Connection Status not ok: " + statusLine.getStatusCode());
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }
        return json.getJSONArray("items");
    }
}
