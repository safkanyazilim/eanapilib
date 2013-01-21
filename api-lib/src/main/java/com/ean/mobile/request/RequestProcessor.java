/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ean.mobile.Constants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;

/**
 * Responsible for the logic that executes requests through the EAN API.
 */
public final class RequestProcessor {

    /**
     * Constructor to prevent instantiation.
     */
    private RequestProcessor() {
        // empty code block
    }

    /**
     * Executes a request using the data provided in the Request object.
     * @param request contains all necessary data to execute a request and parse a response
     * @param <T> the response data
     * @return a response object populated by the JSON data retrieved from the API
     * @throws EanWsError thrown if any error messages are returned via the API call
     */
    public static <T> T run(final Request<T> request) throws EanWsError {
        try {
            final JSONObject jsonResponse = performApiRequest(request);
            return request.consume(jsonResponse);
        } catch (JSONException jsone) {
            Log.e("Unable to process JSON", jsone.getMessage());
        } catch (UrlRedirectionException ure) {
            Log.e("URL redirection problem", ure.getMessage());
        } catch (IOException ioe) {
            Log.e("Could not read response from API", ioe.getMessage());
        } catch (URISyntaxException use) {
            Log.e("Improper URI syntax", use.getMessage());
        }
        return null;
    }

    /**
     * Performs an api request at the specified path with the parameters listed.
     * @param request contains all necessary data to execute a request and parse a response
     * @return The String representation of the JSON returned from the request.
     * @throws IOException If there is a network error or some other connection issue.
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     * @throws URISyntaxException thrown if the URI cannot be built
     */
    private static String performApiRequestForString(final Request request)
            throws IOException, UrlRedirectionException, URISyntaxException {
        //Build the url
        final URLConnection connection;
        final long startTime = System.currentTimeMillis();
        connection = request.getUri().toURL().openConnection();
        if (request.isSecure()) {
            // cause booking requests to use post.
            connection.setDoOutput(true);
            ((HttpURLConnection) connection).setRequestMethod("POST");
            ((HttpURLConnection) connection).setFixedLengthStreamingMode(0);
        }
        // force application/json
        connection.setRequestProperty("Accept", "application/json, */*");

        Log.d(Constants.DEBUG_TAG, "request endpoint: " + connection.getURL().getHost());
        final String jsonString;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //before we go further, we must check to see if we were redirected.
            if (!request.getUri().getHost().equals(connection.getURL().getHost())) {
                // then we were redirected!!
                throw new UrlRedirectionException();
            }
            final StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            jsonString = jsonBuilder.toString();
        } finally {
            // Always close the connection.
            ((HttpURLConnection) connection).disconnect();
        }
        final long timeTaken = System.currentTimeMillis() - startTime;
        Log.d(Constants.DEBUG_TAG, "Took " + timeTaken + " milliseconds.");
        return jsonString;
    }

    /**
     * Performs an API request.
     * @param request contains all necessary data to execute a request and parse a response
     * @return The JSONObject that represents the content returned by the API
     * @throws IOException If there is a network issue, or the network stream cannot otherwise be read.
     * @throws JSONException If the response does not contain valid JSON
     * @throws EanWsError If the response contains an EanWsError element
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     * @throws URISyntaxException thrown if the URI cannot be built
     */
    private static JSONObject performApiRequest(final Request request)
            throws IOException, JSONException, EanWsError, UrlRedirectionException, URISyntaxException {
        final JSONObject response = new JSONObject(performApiRequestForString(request));
        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }
        return response;
    }

}
