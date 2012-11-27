/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.ean.mobile.Constants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UriCreationException;

public abstract class Request {
    //TODO: These should be defined in a .properties file.
    protected static final String CID = "55505";
    protected static final String MINOR_REV = "20";
    protected static final String API_KEY = "cbrzfta369qwyrm9t5b8y8kf";
    protected static final String LOCALE = "it_IT";
    protected static final String CURRENCY_CODE = "USD";

    protected static final String URI_SCHEME = "http";
    //protected static final String URI_HOST = "stg1-www.travelnow.com";
    //protected static final String URI_HOST = "stg5-www.travelnow.com";
    //protected static final String URI_HOST = "xml.travelnow.com";
    protected static final String URI_HOST = "mobile.eancdn.com";
    protected static final String URI_BASE_PATH = "/ean-services/rs/hotel/v3/";
    protected static final String DATE_FORMAT_STRING = "%1$tm/%1$td/%1$tY";


    protected static final URI FULL_URI;

    static {
        URI fullUri = null;
        try {
            fullUri = new URI(URI_SCHEME, URI_HOST, URI_BASE_PATH, null, null);
        } catch (URISyntaxException use) {
            Log.d(Constants.DEBUG_TAG, "Base uri is malformed");
        }
        FULL_URI = fullUri;
    }

    private static String performApiRequestForString(final String relativePath, final List<NameValuePair> params)
            throws IOException, JSONException {
        //Build the url
        final HttpGet getRequest = new HttpGet(createFullUri(FULL_URI, relativePath, params));
        getRequest.setHeader("Accept", "application/json, */*");
        Log.d(Constants.DEBUG_TAG, "uri: " + getRequest.getURI());
        Log.d(Constants.DEBUG_TAG, "getting response");
        final long startTime = System.currentTimeMillis();
        final HttpResponse response = new DefaultHttpClient().execute(getRequest);
        Log.d(Constants.DEBUG_TAG, "got response");
        final StatusLine statusLine = response.getStatusLine();
        final String jsonString;
        try {
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                jsonString = out.toString();
            } else {
                jsonString = null;
                throw new IOException(statusLine.getReasonPhrase());
            }
        } finally {
            // Always close the connection.
            response.getEntity().getContent().close();
        }
        final long timeTaken = System.currentTimeMillis() - startTime;
        Log.d(Constants.DEBUG_TAG, "Took " + timeTaken + " milliseconds.");
        return jsonString;
    }

    /**
     * Performs an API request.
     * @param relativePath The relative path on which to perform the query.
     * @param params The URI parameters to attach to the URI. Used as parameters to the request.
     * @return The JSONObject that represents the content returned by the API
     * @throws IOException If there is a network issue, or the network stream cannot otherwise be read.
     * @throws JSONException If the response does not contain valid JSON
     * @throws EanWsError If the response contains an EanWsError element
     */
    protected static JSONObject performApiRequest(final String relativePath, final List<NameValuePair> params)
            throws IOException, JSONException, EanWsError {
        final JSONObject response = new JSONObject(performApiRequestForString(relativePath, params));
        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }
        return response;
    }

    protected static URI createFullUri(final URI baseUri,
                                       final String relativePath,
                                       final List<NameValuePair> params) {
        if (baseUri == null) {
            return null;
        }
        final URI relativeUri = relativePath == null ? baseUri : baseUri.resolve(relativePath);
        final String queryString = createQueryString(params);

        try {
            return new URI(relativeUri.getScheme(), relativeUri.getHost(), relativeUri.getPath(), queryString, null);
        } catch (URISyntaxException use) {
            throw new UriCreationException("Full URI could not be created for the request.", use);
        }
    }

    private static String createQueryString(final List<NameValuePair> params) {
        String queryString = null;
        if (params != null && !params.isEmpty()) {
            final StringBuilder sb = new StringBuilder(params.size() * 10);
            for (NameValuePair param : params) {
                if (param == null) {
                    continue;
                }
                sb.append(param.getName());
                sb.append("=");
                sb.append(param.getValue() == null ? "" : param.getValue());
                sb.append("&");
            }
            String potentialQueryString = sb.toString();
            if (potentialQueryString.length() == 0) {
                potentialQueryString = null;
            } else if (potentialQueryString.endsWith("&")) {
                potentialQueryString = potentialQueryString.substring(0, potentialQueryString.length() - 1);
            }
            queryString = potentialQueryString;
            //queryString = URLEncodedUtils.format(params, "UTF-8").replace("%", "\\%");
        }
        return queryString;
    }

    public static String formatApiDate(final Calendar cal) {
        return String.format(DATE_FORMAT_STRING, cal);
    }
}