/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.ean.mobile.Constants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UriCreationException;
import com.ean.mobile.exception.UrlRedirectionException;

/**
 * The base class for all of the API requests that are implemented. Provides some easy-to use methods for performing
 * requests.
 */
public abstract class Request {
    private static final List<NameValuePair> BASIC_URL_PARAMETERS;

    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

    private static final URI STANDARD_ENDPOINT;

    private static final URI SECURE_ENDPOINT;

    static {
        final String standardUriScheme = "http";
        final String standardUriHost = "api.ean.com";
        final String secureUriScheme = "https";
        final String secureUriHost = "book.api.ean.com";
        final String uriBasePath = "/ean-services/rs/hotel/v3/";

        URI standardUri = null;
        URI secureUri = null;
        try {
            standardUri = new URI(standardUriScheme, standardUriHost, uriBasePath, null, null);
            secureUri = new URI(secureUriScheme, secureUriHost, uriBasePath, null, null);
        } catch (URISyntaxException use) {
            // This exception can only be thrown if the static variables listed above are incorrectly
            // formatted, or the usage of new URI(...) is incorrect, both of which should be found out
            // long before the code is used in production, since the requests (particularly the int tests)
            // would fail.
            Log.wtf(Constants.DEBUG_TAG, "Base uri is malformed");
        }
        STANDARD_ENDPOINT = standardUri;
        SECURE_ENDPOINT = secureUri;

        //TODO: load CID, APIKey, and customerUserAgent from the classpath
        final String cid = "55505";
        final String apiKey = "cbrzfta369qwyrm9t5b8y8kf";
        final String customerUserAgent = "Android";

        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("cid", cid),
            new BasicNameValuePair("apiKey", apiKey),
            new BasicNameValuePair("minorRev", "20"),
            new BasicNameValuePair("customerUserAgent", customerUserAgent)
        );
        BASIC_URL_PARAMETERS = Collections.unmodifiableList(urlParameters);
    }


    /**
     * Performs an api request at the specified path with the parameters listed.
     * @param relativePath The path on which to perform the request.
     * @param params The URI parameters to pass in the request.
     * @return The String representation of the JSON returned from the request.
     * @throws IOException If there is a network error or some other connection issue.
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     */
    private static String performApiRequestForString(final String relativePath,
                                                     final List<NameValuePair> params)
            throws IOException, UrlRedirectionException {
        //TODO: refactor this to use java.net.HttpUrlConnection and javax.net.ssl.HttpsUrlConnection
        //Build the url
        final URLConnection connection;
        final long startTime = System.currentTimeMillis();
        final URI endpoint;
        if ("res".equals(relativePath)) {
            endpoint = SECURE_ENDPOINT;
        } else {
            endpoint = STANDARD_ENDPOINT;
        }
        connection = createFullUri(endpoint, relativePath, params).toURL().openConnection();
        if ("res".equals(relativePath)) {
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
            if (!endpoint.getHost().equals(connection.getURL().getHost())) {
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
     * @param relativePath The relative path on which to perform the query.
     * @param params The URI parameters to attach to the URI. Used as parameters to the request.
     * @return The JSONObject that represents the content returned by the API
     * @throws IOException If there is a network issue, or the network stream cannot otherwise be read.
     * @throws JSONException If the response does not contain valid JSON
     * @throws EanWsError If the response contains an EanWsError element
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     */
    protected static JSONObject performApiRequest(final String relativePath, final List<NameValuePair> params)
            throws IOException, JSONException, EanWsError, UrlRedirectionException {
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
    protected static URI createFullUri(final URI baseUri, final String relativePath, final List<NameValuePair> params) {
        if (baseUri == null) {
            return null;
        }
        final URI relativeUri = relativePath == null ? baseUri : baseUri.resolve(relativePath);
        //URLEncodedUtils cannot be used because the api cannot handle %2F instead of / for the date strings.
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
            // URLEncoder.encode cannot be used since it encodes things in a way the api does not expect, particularly
            // dates.
            queryString = potentialQueryString;
        }
        return queryString;
    }

    /**
     * Gets the url parameters that will need to be present for every request.
     * @param locale The locale in which to request.
     * @param currencyCode The currency code in which to perform this request.
     * @param arrivalDate The arrival date for this request.
     * @param departureDate The departure date for this request.
     * @return The above parameters plus the cid, apikey, minor rev, and customer user agent url parameters.
     */
    public static List<NameValuePair> getBasicUrlParameters(final String locale, final String currencyCode,
            final LocalDate arrivalDate, final LocalDate departureDate) {
        //TODO: force locale to be a java Locale object?
        final List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.addAll(BASIC_URL_PARAMETERS);
        if (locale != null) {
            params.add(new BasicNameValuePair("locale", locale));
        }
        if (currencyCode != null) {
            params.add(new BasicNameValuePair("currencyCode", currencyCode));
        }
        if (arrivalDate != null) {
            params.add(new BasicNameValuePair("arrivalDate", DATE_TIME_FORMATTER.print(arrivalDate)));
        }
        if (departureDate != null) {
            params.add(new BasicNameValuePair("departureDate", DATE_TIME_FORMATTER.print(departureDate)));
        }
        return params;
    }

    /**
     * Gets the url parameters that will need to be present for every request.
     * @param locale The locale in which to request.
     * @param currencyCode The currency code in which to perform this request.
     * @return The above parameters plus the cid, apikey, minor rev, and customer user agent url parameters.
     */
    public static List<NameValuePair> getBasicUrlParameters(final String locale, final String currencyCode) {
        return getBasicUrlParameters(locale, currencyCode, null, null);
    }

    /**
     * Gets the url parameters that will need to be present for every request.
     * @return The above parameters plus the cid, apikey, minor rev, and customer user agent url parameters.
     */
    public static List<NameValuePair> getBasicUrlParameters() {
        return getBasicUrlParameters(null, null, null, null);
    }
}
