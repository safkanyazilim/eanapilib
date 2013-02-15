/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.ean.mobile.Constants;
import com.ean.mobile.exception.CommonParameterValidationException;

/**
 * Contains elements that (1) are common to all requests and (2) typically remain the same across multiple requests.
 */
public final class CommonParameters {

    /**
     * The CID to use for API requests. Required for all API calls.
     */
    public static volatile String cid;

    /**
     * The API key to use for API requests. Required for all API calls.
     */
    public static volatile String apiKey;

    /**
     * The user agent to use for API requests.
     */
    public static volatile String customerUserAgent;

    /**
     * The locale to use for API requests.
     */
    public static volatile String locale;

    /**
     * The currency code to use for API requests.
     */
    public static volatile String currencyCode;

    /**
     * The customer IP address to use for API requests.
     */
    public static volatile String customerIpAddress;

    /**
     * The session ID to use for API requests. Set automatically when returned as part of a hotel list response.
     */
    public static volatile String customerSessionId;

    /**
     * The minor revision to use for API requests. The library has only been tested with the default minor revision,
     * so this should only be changed if absolutely necessary!
     */
    public static volatile String minorRev = Constants.MINOR_REV;

    /**
     * Private, no-op constructor to prevent instantiation.
     */
    private CommonParameters() {

    }

    /**
     * Convenience method that returns all set parameters as a list of NameValuePair objects for use in API requests.
     *
     * @return a list of NameValuePair objects containing each attribute name and value.
     */
    public static List<NameValuePair> asNameValuePairs() {
        validateParameters();

        final List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("cid", cid));
        nameValuePairs.add(new BasicNameValuePair("apiKey", apiKey));

        if (customerUserAgent != null) {
            nameValuePairs.add(new BasicNameValuePair("customerUserAgent", customerUserAgent));
        }
        if (locale != null) {
            nameValuePairs.add(new BasicNameValuePair("locale", locale));
        }
        if (currencyCode != null) {
            nameValuePairs.add(new BasicNameValuePair("currencyCode", currencyCode));
        }
        if (customerIpAddress != null) {
            nameValuePairs.add(new BasicNameValuePair("customerIpAddress", customerIpAddress));
        }
        if (customerSessionId != null) {
            nameValuePairs.add(new BasicNameValuePair("customerSessionId", customerSessionId));
        }
        if (minorRev != null) {
            nameValuePairs.add(new BasicNameValuePair("minorRev", minorRev));
        }

        return nameValuePairs;
    }

    /**
     * Checks that all variables necessary to execute a request have been initialized. If not,
     * a CommonParameterValidationException is thrown.
     */
    private static void validateParameters() {
        if (cid == null || apiKey == null) {
            throw new CommonParameterValidationException(
                "You MUST initialize both the cid and apiKey in CommonParameters before performing any requests!");
        }
    }

}
