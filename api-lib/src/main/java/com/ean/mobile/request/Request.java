/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.ean.mobile.Constants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UriCreationException;

/**
 * The base class for all of the API requests that are implemented. Provides some easy-to use methods for performing
 * requests.
 */
public abstract class Request {
    //TODO: These should be defined in a .properties file.
    protected static final String CID = "55505";
    protected static final String MINOR_REV = "20";
    protected static final String API_KEY = "cbrzfta369qwyrm9t5b8y8kf";
    protected static final String LOCALE = "en_US";
    protected static final String CURRENCY_CODE = "USD";
    //TODO: Add customeruseragent so requests can be cataloged as mobile
    protected static final String CUSTOMER_USER_AGENT = "Android";

    protected static final String STANDARD_URI_SCHEME = "http";
    //protected static final String STANDARD_URI_SCHEME = "https";
    //protected static final String STANDARD_URI_HOST = "stg1-www.travelnow.com";
    //protected static final String STANDARD_URI_HOST = "stg5-www.travelnow.com";
    //protected static final String STANDARD_URI_HOST = "xml.travelnow.com";
    protected static final String STANDARD_URI_HOST = "api.ean.com";


    protected static final String SECURE_URI_SCHEME = "https";
    //protected static final String SECURE_URI_HOST = "stg1-www.travelnow.com";
    protected static final String SECURE_URI_HOST = "book.api.ean.com";


    protected static final String URI_BASE_PATH = "/ean-services/rs/hotel/v3/";
    protected static final String DATE_FORMAT_STRING = "MM/dd/yyyy";

    protected static final URI STANDARD_ENDPOINT;

    protected static final URI SECURE_ENDPOINT;

    protected static final List<NameValuePair> BASIC_URL_PARAMETERS;

    static {
        URI standardUri = null;
        URI secureUri = null;
        try {
            standardUri = new URI(STANDARD_URI_SCHEME, STANDARD_URI_HOST, URI_BASE_PATH, null, null);
            secureUri = new URI(SECURE_URI_SCHEME, SECURE_URI_HOST, URI_BASE_PATH, null, null);
        } catch (URISyntaxException use) {
            // This exception can only be thrown if the static variables listed above are incorrectly
            // formatted, or the usage of new URI(...) is incorrect, both of which should be found out
            // long before the code is used in production, since the requests (particularly the int tests)
            // would fail.
            Log.wtf(Constants.DEBUG_TAG, "Base uri is malformed");
        }
        STANDARD_ENDPOINT = standardUri;
        SECURE_ENDPOINT = secureUri;
        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("cid", CID),
                new BasicNameValuePair("minorRev", MINOR_REV),
                new BasicNameValuePair("apiKey", API_KEY),
                new BasicNameValuePair("locale", LOCALE),
                new BasicNameValuePair("currencyCode", CURRENCY_CODE)
                //new BasicNameValuePair("customerUserAgent", CUSTOMER_USER_AGENT)
        );
        BASIC_URL_PARAMETERS = Collections.unmodifiableList(urlParameters);
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

    /**
     * Performs an api request at the specified path with the parameters listed.
     * @param relativePath The path on which to perform the request.
     * @param params The URI parameters to pass in the request.
     * @return The String representation of the JSON returned from the request.
     * @throws IOException If there is a network error or some other connection issue.
     */
    private static String performApiRequestForString(final String relativePath, final List<NameValuePair> params)
            throws IOException {
        //Build the url
        final HttpRequestBase request;
        if ("res".equals(relativePath)) {
            request = new HttpPost(createFullUri(SECURE_ENDPOINT, relativePath, params));
        } else {
            request = new HttpGet(createFullUri(STANDARD_ENDPOINT, relativePath, params));
        }
        request.setHeader("Accept", "application/json, */*");
        System.out.println(request.getURI());
        Log.d(Constants.DEBUG_TAG, "endpoint: " + request.getURI().getHost());
        Log.d(Constants.DEBUG_TAG, "getting response");
        final long startTime = System.currentTimeMillis();
        final HttpResponse response = new DefaultHttpClient().execute(request);
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
            if (response.getEntity().isStreaming()) {
                response.getEntity().getContent().close();
            }
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

    /**
     * Creates a full url based on the baseuri, the relative path and the uri parameters to pass.
     * @param baseUri The URI to use as the base.
     * @param relativePath The relative path from the base uri to finally request.
     * @param params The URI parameters to include in the query string.
     * @return The fully-formed URI based on the inputs.
     */
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

    /**
     * Creates the query portion of a URI based on a list of NameValuePairs.
     * @param params The parameters to turn into a query string.
     * @return The requested string.
     */
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
        }
        return queryString;
    }

    /**
     * Formats a DateTime object as a date string expected by the API.
     * @param dateTime The DateTime object to format.
     * @return The date string for the API.
     */
    public static String formatApiDate(final DateTime dateTime) {
        return DATE_TIME_FORMATTER.print(dateTime);
    }
}