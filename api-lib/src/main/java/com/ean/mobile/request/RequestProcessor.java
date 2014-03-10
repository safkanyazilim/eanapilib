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
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
 */

package com.ean.mobile.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URLConnection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.Constants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;

/**
 * Responsible for the logic that executes requests through the EAN API.
 */
public final class RequestProcessor {
	private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
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
     * @throws UrlRedirectionException thrown if the request is redirected (possibly due to a network issue)
     */
    public static <T> T run(final Request<T> request) throws EanWsError, UrlRedirectionException {
        try {
            final JSONObject jsonResponse = performApiRequest(request);
            return request.consume(jsonResponse);
        } catch (JSONException jsone) {
            logger.error("Unable to process JSON", jsone);
        } catch (IOException ioe) {
        	logger.error("Could not read response from API", ioe);
        } catch (URISyntaxException use) {
        	logger.error("Improper URI syntax", use);
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
        if (request.requiresSecure()) {
            // cause booking requests to use post.
            connection.setDoOutput(true);
            ((HttpURLConnection) connection).setRequestMethod("POST");
            ((HttpURLConnection) connection).setFixedLengthStreamingMode(0);
        }
        // force application/json
        connection.setRequestProperty("Accept", "application/json, */*");

        logger.debug("request endpoint: " + connection.getURL().getHost());
        final String jsonString;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //before we go further, we must check to see if we were redirected.
            if (!request.getUri().getHost().equals(connection.getURL().getHost())
                    && !request.isTolerantOfUriRedirections()) {
                // then we were redirected and we can't tolerate it!!
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
        logger.debug("Took " + timeTaken + " milliseconds.");
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
