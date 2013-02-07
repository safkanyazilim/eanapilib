/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import java.util.Currency;
import java.util.Locale;

/**
 * Contains basic elements that (1) are common to all requests and (2) remain the same across multiple requests.
 */
public final class BaseRequest {

    private static BaseRequest instance;

    private final String cid;
    private final String apiKey;
    private final String locale;
    private final String currencyCode;

    /**
     * Constructor that sets all fields.
     *
     * @param cid the CID to set.
     * @param apiKey the API key to set.
     * @param locale the locale to set.
     * @param currency contains the currency code to set.
     */
    private BaseRequest(final String cid, final String apiKey, final Locale locale, final Currency currency) {
        this.cid = cid;
        this.apiKey = apiKey;
        this.locale = locale == null ? null : locale.toString();
        this.currencyCode = currency == null ? null : currency.getCurrencyCode();
    }

    /**
     * Builds a new BaseRequest and populates it with the specified values. This method MUST be called before
     * performing any requests!
     *
     * @param cid the CID to use.
     * @param apiKey the API key to use.
     * @param locale the locale to use.
     * @param currency the currency to use.
     */
    public static void initialize(
            final String cid, final String apiKey, final Locale locale, final Currency currency) {
        instance = new BaseRequest(cid, apiKey, locale, currency);
    }

    /**
     * Retrieves a singleton BaseRequest or throws a RuntimeException if it has not been initialized.
     *
     * @return a BaseRequest object that has already been initialized.
     */
    private static BaseRequest getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                "BaseRequest is not initialized. You MUST call BaseRequest.initialize before performing any requests!");
        }
        return instance;
    }

    /**
     * Retrieves the CID previously set by the initialize method.
     *
     * @return the CID previously set, or null if none was set.
     */
    public static String getCid() {
        return getInstance().cid;
    }

    /**
     * Retrieves the API key previously set by the initialize method.
     *
     * @return the API key previously set, or null if none was set.
     */
    public static String getApiKey() {
        return getInstance().apiKey;
    }

    /**
     * Retrieves the locale previously set by the initialize method.
     *
     * @return the locale previously set, or null if none was set.
     */
    public static String getLocale() {
        return getInstance().locale;
    }

    /**
     * Retrieves the currency code previously set by the initialize method.
     *
     * @return the currency code previously set, or null if none was set.
     */
    public static String getCurrencyCode() {
        return getInstance().currencyCode;
    }
}
