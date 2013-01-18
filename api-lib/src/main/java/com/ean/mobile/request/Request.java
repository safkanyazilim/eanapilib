/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.net.URI;
import java.net.URISyntaxException;
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

/**
 * The base class for all of the API requests that are implemented. Provides some easy-to use methods for performing
 * requests.
 * @param <T> a response object used to store converted JSON data
 */
public abstract class Request<T> {

    /**
     * The standard endpoint for a non-secure request.
     */
    public static final URI STANDARD_ENDPOINT;

    /**
     * The endpoint for a secure request.
     */
    public static final URI SECURE_ENDPOINT;

    /**
     * A formatter to use for all date/string conversions.
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER;

    private static final List<NameValuePair> BASIC_URL_PARAMETERS;
    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy";
    private List<NameValuePair> urlParameters;

    static {
        final String standardUriScheme = "http";
        final String standardUriHost = "api.ean.com";
        final String secureUriScheme = "https";
        final String secureUriHost = "book.api.ean.com";
        final String uriBasePath = "/ean-services/rs/hotel/v3/";

        DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

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
     * Parses the specified JSON and returns the appropriate object containing the response data.
     * @param jsonObject a raw JSON response
     * @return an object representing the converted JSON data
     * @throws JSONException thrown if the provided JSON data cannot be parsed
     * @throws EanWsError thrown if an error message is included in the JSON data
     */
    public abstract T consume(final JSONObject jsonObject) throws JSONException, EanWsError;

    /**
     * Returns the path name of the implemented request.
     * @return a path representing the request (info, res, avail, etc)
     */
    public abstract String getPath();

    /**
     * Return true if the implemented request should use a secure connection.
     * @return true if secure, false if not
     */
    public abstract boolean isSecure();

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

    /**
     * Getter method for urlParameters.
     * @return urlParameters
     */
    public List<NameValuePair> getUrlParameters() {
        return urlParameters;
    }

    /**
     * Setter method for urlParameters.
     * @param urlParameters the new value for urlParameters
     */
    public void setUrlParameters(final List<NameValuePair> urlParameters) {
        this.urlParameters = urlParameters;
    }
}
