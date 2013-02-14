/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import java.util.Currency;
import java.util.Locale;

/**
 * Contains various constants for use in unit and integration tests.
 */
public final class TestConstants {

    public static final String CID = "55505";
    public static final String API_KEY = "cbrzfta369qwyrm9t5b8y8kf";
    public static final String SHARED_SECRET = "YOUR_SECRET";
    public static final String CUSTOMER_IP_ADDRESS = "127.0.0.1";
    public static final String CUSTOMER_USER_AGENT = "Android";
    public static final String CUSTOMER_SESSION_ID = "0ABAA856-83A7-F691-3C82-29AA53292D64";
    public static final Locale LOCALE = Locale.US;
    public static final Currency CURRENCY = Currency.getInstance(LOCALE);

    /**
     * Private, no-op constructor to prevent instantiation.
     */
    private TestConstants() {

    }
}
