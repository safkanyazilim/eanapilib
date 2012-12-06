/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds data regarding a nightly rate for a room.
 */
public final class NightlyRate {

    /**
     * Whether or not this rate is a promotional rate.
     */
    public final boolean promo;

    /**
     * The actual rate for this nightly rate.
     */
    public final BigDecimal rate;

    /**
     * The base rate (pre-promo) for this nightly rate.
     */
    public final BigDecimal baseRate;

    /**
     * The main constructor, building objects from a JSONObject.
     * @param nightlyRateJson The JSON representing this nightly rate.
     * @throws JSONException If there is an error in the JSON
     */
    public NightlyRate(final JSONObject nightlyRateJson) throws JSONException {
        this.promo = nightlyRateJson.getBoolean("@promo");
        this.rate = new BigDecimal(nightlyRateJson.getString("@rate"));
        this.baseRate = new BigDecimal(nightlyRateJson.getString("@baseRate"));
    }

    /**
     * Constructs a list of nightly rates from a JSONArray of nightly rates.
     * @param nightlyRatesJson The JSON representing the array of nightly rates
     * @return The NightlyRate objects parsed from the array.
     * @throws JSONException If there is an error in the JSON.
     */
    public static List<NightlyRate> parseNightlyRates(final JSONArray nightlyRatesJson) throws JSONException {
        final List<NightlyRate> nightlyRates = new ArrayList<NightlyRate>(nightlyRatesJson.length());
        for (int j = 0; j < nightlyRatesJson.length(); j++) {
            nightlyRates.add(new NightlyRate(nightlyRatesJson.getJSONObject(j)));
        }
        return nightlyRates;
    }

    /**
     * Constructs a singleton list of NightlyRates from the JSONObject of nightly rate.
     * @param nightlyRatesJson The JSON representing the nightly rate
     * @return The NightlyRate object parsed from the object.
     * @throws JSONException If there is an error in the JSON.
     */
    public static List<NightlyRate> parseNightlyRates(final JSONObject nightlyRatesJson) throws JSONException {
        return Collections.singletonList(new NightlyRate(nightlyRatesJson.getJSONObject("NightlyRate")));
    }
}

