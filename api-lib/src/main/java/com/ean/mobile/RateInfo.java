/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds the information about rates for a particular availability. Has information about both nightly rates
 * and surcharges, as well as the specified currency code and whether or not the rate is a promo.
 */
public final class RateInfo {

    /**
     * The list of nightly rates for the current rate. Populated with Collections.unmodifiableList();
     */
    public final List<NightlyRate> nightlyRates;

    /**
     * The currency code set at construction time.
     */
    public final String currencyCode;

    /**
     * Whether or not this rate represents a promo rate.
     */
    public final boolean promo;

    /**
     * The map of surcharges (fees) and their names.
     */
    public final Map<String, BigDecimal> surcharges;

    /**
     * Constructs a RateInfo from a JSONObject.
     * @param rateInfoJson The JSONObject holding the data to construct this object with.
     * @throws JSONException If the JSON isn't configured correctly.
     */
    public RateInfo(final JSONObject rateInfoJson) throws JSONException {
        final JSONObject chargeable = rateInfoJson.getJSONObject("ChargeableRateInfo");

        final String nightlyRatesPerRoom = "NightlyRatesPerRoom";
        final String nightlyRate = "NightlyRate";

        final List<NightlyRate> localNightlyRates = new ArrayList<NightlyRate>();

        if (chargeable.optJSONArray(nightlyRatesPerRoom) != null) {
            localNightlyRates.addAll(NightlyRate.parseNightlyRates(chargeable.getJSONArray(nightlyRatesPerRoom)));
        } else if (chargeable.optJSONObject(nightlyRatesPerRoom) != null) {
            if (chargeable.getJSONObject(nightlyRatesPerRoom).optJSONArray(nightlyRate) != null) {
                localNightlyRates.addAll(
                    NightlyRate.parseNightlyRates(
                        chargeable.getJSONObject(nightlyRatesPerRoom).getJSONArray(nightlyRate)));
            } else {
                localNightlyRates.addAll(NightlyRate.parseNightlyRates(chargeable.getJSONObject(nightlyRatesPerRoom)));
            }
        }

        final Map<String, BigDecimal> localSurcharges = new HashMap<String, BigDecimal>();
        if (chargeable.optJSONArray("Surcharges") != null) {
            for (int i = 0; i < chargeable.optJSONArray("Surcharges").length(); i++) {
                final JSONObject surchargeJson = chargeable.optJSONArray("Surcharges").getJSONObject(i);
                localSurcharges.put(surchargeJson.getString("@type"),
                               new BigDecimal(surchargeJson.getString("@amount")));
            }
        } else if (chargeable.optJSONObject("Surcharge") != null) {
            localSurcharges.put(
                chargeable.optJSONObject("Surcharge").getString("@type"),
                new BigDecimal(chargeable.optJSONObject("Surcharge").getString("@amount")));
        }


        promo = rateInfoJson.getBoolean("@promo");
        currencyCode = chargeable.getString("@currencyCode");
        nightlyRates = Collections.unmodifiableList(localNightlyRates);
        surcharges = Collections.unmodifiableMap(localSurcharges);
    }

    /**
     * Parses a list of RateInfo objects from a JSONArray representing their data.
     * @param rateInfosJson The json from which to parse
     * @return The RateInfo objects represented by the JSONArray.
     * @throws JSONException If the JSON in the JSONArray is not formatted in the way expected by the parser
     */
    public static List<RateInfo> parseRateInfos(final JSONArray rateInfosJson) throws JSONException {
        final List<RateInfo> rateInfos = new ArrayList<RateInfo>(rateInfosJson.length());
        for (int j = 0; j < rateInfosJson.length(); j++) {
            rateInfos.add(new RateInfo(rateInfosJson.getJSONObject(j)));
        }
        return rateInfos;
    }

    /**
     * Parses a singleton list of rateinfo from a JSONObject. Needed because when there is only one RateInfo,
     * the RateInfos get represented as a single RateInfo object rather than an array of size one.
     * @param rateInfosJson The json from which to parse.
     * @return The singletonList of the RateInfo represented by the JSON
     * @throws JSONException If the JSON is not as expected.
     */
    public static List<RateInfo> parseRateInfos(final JSONObject rateInfosJson) throws JSONException {
        return Collections.singletonList(new RateInfo(rateInfosJson.getJSONObject("RateInfo")));
    }

    /**
     * Gets the average rate of all of the nightly rates.
     * @return The average rate calculated from all the nightly rates.
     */
    public BigDecimal getAverageRate() {
        BigDecimal avgRate = BigDecimal.ZERO;
        if (nightlyRates.isEmpty()) {
            return avgRate;
        }
        for (NightlyRate rate : nightlyRates) {
            avgRate = avgRate.add(rate.rate);
        }
        avgRate = avgRate.divide(new BigDecimal(nightlyRates.size()), 2, RoundingMode.HALF_EVEN);
        return avgRate;
    }

    /**
     * Gets the average base rate of all of the nightly rates.
     * @return The average base rate calculated from all the nightly rates.
     */
    public BigDecimal getAverageBaseRate() {
        BigDecimal avgBaseRate = BigDecimal.ZERO;
        if (nightlyRates.isEmpty()) {
            return avgBaseRate;
        }
        for (NightlyRate rate : nightlyRates) {
            avgBaseRate = avgBaseRate.add(rate.baseRate);
        }
        avgBaseRate = avgBaseRate.divide(new BigDecimal(nightlyRates.size()), 2, RoundingMode.HALF_EVEN);
        return avgBaseRate;
    }

    /**
     * Determines whether the average rate and the average base rate are equal.
     * @return Whether or not the average rates are equal.
     */
    public boolean areAverageRatesEqual() {
        return this.getAverageRate().equals(this.getAverageBaseRate());
    }

    /**
     * Gets the total of all of the nightly rates.
     * @return The total.
     */
    public BigDecimal getRateTotal() {
        BigDecimal rateTotal = BigDecimal.ZERO;
        for (NightlyRate rate : nightlyRates) {
            rateTotal = rateTotal.add(rate.rate);
        }
        return rateTotal;
    }

    /**
     * Gets the total of all of the base nightly rates.
     * @return The total.
     */
    public BigDecimal getBaseRateTotal() {
        BigDecimal baseRateTotal = BigDecimal.ZERO;
        for (NightlyRate rate : nightlyRates) {
            baseRateTotal = baseRateTotal.add(rate.baseRate);
        }
        return baseRateTotal;
    }
}
