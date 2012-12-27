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
public final class Rate {

    /**
     * Whether or not this rate represents a promo rate.
     */
    public final boolean promo;

    /**
     * The rate information that is actually chargeable.
     */
    public final RateInfo chargeable;

    /**
     * The rate information, converted to the requested currency.
     */
    public final RateInfo converted;

    /**
     * The list of rooms to which this rate is applied.
     */
    public final List<Room> roomGroup;


    /**
     * Constructs a Rate from a JSONObject.
     * @param rateInfoJson The JSONObject holding the data to construct this object with.
     * @throws JSONException If the JSON isn't configured correctly.
     */
    public Rate(final JSONObject rateInfoJson) throws JSONException {

        final List<Room> localRooms;
        if (rateInfoJson.optJSONArray("RoomGroup") != null) {
            final JSONArray roomGroupJson = rateInfoJson.optJSONArray("RoomGroup");
            localRooms = new ArrayList<Room>(roomGroupJson.length());
            for (int i = 0; i < roomGroupJson.length(); i++) {
                localRooms.add(new Room(roomGroupJson.getJSONObject(i)));
            }
        } else if (rateInfoJson.optJSONObject("RoomGroup") != null) {
            localRooms
                = Collections.singletonList(new Room(rateInfoJson.optJSONObject("RoomGroup").optJSONObject("Room")));
        } else {
            localRooms = Collections.emptyList();
        }

        final JSONObject chargeableObject = rateInfoJson.optJSONObject("ChargeableRateInfo");
        final JSONObject convertedObject = rateInfoJson.optJSONObject("ConvertedRateInfo");

        chargeable = new RateInfo(chargeableObject);
        converted = convertedObject == null ? null : new RateInfo(convertedObject);

        promo = rateInfoJson.getBoolean("@promo");
        roomGroup = Collections.unmodifiableList(localRooms);
    }

    /**
     * Parses a list of Rate objects from a JSONArray representing their data.
     * @param rateInfosJson The json from which to parse
     * @return The Rate objects represented by the JSONArray.
     * @throws JSONException If the JSON in the JSONArray is not formatted in the way expected by the parser
     */
    public static List<Rate> parseRates(final JSONArray rateInfosJson) throws JSONException {
        final List<Rate> rateInfos = new ArrayList<Rate>(rateInfosJson.length());
        for (int j = 0; j < rateInfosJson.length(); j++) {
            rateInfos.add(new Rate(rateInfosJson.getJSONObject(j)));
        }
        return rateInfos;
    }

    /**
     * Parses a singleton list of rateinfo from a JSONObject. Needed because when there is only one Rate,
     * the RateInfos get represented as a single Rate object rather than an array of size one.
     * @param rateInfosJson The json from which to parse.
     * @return The singletonList of the Rate represented by the JSON
     * @throws JSONException If the JSON is not as expected.
     */
    public static List<Rate> parseRates(final JSONObject rateInfosJson) throws JSONException {
        return Collections.singletonList(new Rate(rateInfosJson.getJSONObject("RateInfo")));
    }

    /**
     * This is a holder class for the various rate information to be held by a rate object. Instances of this class
     * will either be "chargeable" or "converted", as noted by the so named fields in {@link Rate}.
     */
    public static class RateInfo {
        /**
         * The list of nightly rates for the current rate. Populated with Collections.unmodifiableList();
         */
        public final List<NightlyRate> nightlyRates;

        /**
         * The currency code set at construction time.
         */
        public final String currencyCode;

        /**
         * The map of surcharges (fees) and their names.
         */
        public final Map<String, BigDecimal> surcharges;

        /**
         * The standard constructor for this object, constructs itself from a JSON object.
         * @param rate The JSONObject representing this object.
         * @throws JSONException If the JSON passed does not have the expected fields populated. This indicates
         * an unexpected api change or a network failure resulting in invalid json.
         */
        public RateInfo(final JSONObject rate) throws JSONException {

            final String nightlyRatesPerRoom = "NightlyRatesPerRoom";
            final String nightlyRate = "NightlyRate";

            final List<NightlyRate> localNightlyRates = new ArrayList<NightlyRate>();

            if (rate.optJSONArray(nightlyRatesPerRoom) != null) {
                localNightlyRates.addAll(NightlyRate.parseNightlyRates(rate.getJSONArray(nightlyRatesPerRoom)));
            } else if (rate.optJSONObject(nightlyRatesPerRoom) != null) {
                if (rate.getJSONObject(nightlyRatesPerRoom).optJSONArray(nightlyRate) != null) {
                    localNightlyRates.addAll(
                            NightlyRate.parseNightlyRates(
                                    rate.getJSONObject(nightlyRatesPerRoom).getJSONArray(nightlyRate)));
                } else {
                    localNightlyRates.addAll(NightlyRate.parseNightlyRates(rate.getJSONObject(nightlyRatesPerRoom)));
                }
            }

            final Map<String, BigDecimal> localSurcharges = new HashMap<String, BigDecimal>();
            if (rate.optJSONArray("Surcharges") != null) {
                for (int i = 0; i < rate.optJSONArray("Surcharges").length(); i++) {
                    final JSONObject surchargeJson = rate.optJSONArray("Surcharges").getJSONObject(i);
                    localSurcharges.put(surchargeJson.getString("@type"),
                            new BigDecimal(surchargeJson.getString("@amount")));
                }
            } else if (rate.optJSONObject("Surcharge") != null) {
                localSurcharges.put(
                        rate.optJSONObject("Surcharge").getString("@type"),
                        new BigDecimal(rate.optJSONObject("Surcharge").getString("@amount")));
            }

            currencyCode = rate.getString("@currencyCode");
            nightlyRates = Collections.unmodifiableList(localNightlyRates);
            surcharges = Collections.unmodifiableMap(localSurcharges);
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

    /**
     * Number of adults, children, and the rate key applied to a particular room.
     */
    public static class Room {
        /**
         * The number of children expected to occupy this room.
         */
        public final int numberOfChildren;

        /**
         * The number of adults expected to occupy this room.
         */
        public final int numberOfAdults;

        /**
         * The rateKey for this room, used for booking.
         */
        public final String rateKey;

        /**
         * The json-based constructor for this object.
         * @param jsonRoom The JSONObject representing this room.
         */
        public Room(final JSONObject jsonRoom) {
            this.numberOfAdults = jsonRoom.optInt("numberOfAdults");
            this.numberOfChildren = jsonRoom.optInt("numberOfChildren");
            this.rateKey = jsonRoom.optString("rateKey");
        }
    }
}
