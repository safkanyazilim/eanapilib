/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
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
     */
    public Rate(final JSONObject rateInfoJson) {

        final List<Room> localRooms;
        final JSONObject roomGroupJson = rateInfoJson.optJSONObject("RoomGroup");
        if (roomGroupJson.optJSONArray("Room") != null) {
            final JSONArray roomJson = roomGroupJson.optJSONArray("Room");
            localRooms = new ArrayList<Room>(roomJson.length());
            for (int i = 0; i < roomJson.length(); i++) {
                localRooms.add(new Room(roomJson.optJSONObject(i)));
            }
        } else if (roomGroupJson.optJSONObject("Room") != null) {
            localRooms
                = Collections.singletonList(new Room(roomGroupJson.optJSONObject("Room")));
        } else {
            localRooms = Collections.emptyList();
        }

        final JSONObject chargeableObject = rateInfoJson.optJSONObject("ChargeableRateInfo");
        final JSONObject convertedObject = rateInfoJson.optJSONObject("ConvertedRateInfo");

        chargeable = new RateInfo(chargeableObject);
        converted = convertedObject == null ? null : new RateInfo(convertedObject);

        promo = rateInfoJson.optBoolean("@promo");
        roomGroup = Collections.unmodifiableList(localRooms);
    }

    /**
     * Parses a list of Rate objects from a JSONArray representing their data.
     * @param rateInfosJson The json from which to parse
     * @return The Rate objects represented by the JSONArray.
     */
    public static List<Rate> parseRates(final JSONArray rateInfosJson) {
        final List<Rate> rateInfos = new ArrayList<Rate>(rateInfosJson.length());
        for (int j = 0; j < rateInfosJson.length(); j++) {
            rateInfos.add(new Rate(rateInfosJson.optJSONObject(j)));
        }
        return rateInfos;
    }

    /**
     * Parses a singleton list of rateinfo from a JSONObject. Needed because when there is only one Rate,
     * the RateInfos get represented as a single Rate object rather than an array of size one.
     * @param rateInfosJson The json from which to parse.
     * @return The singletonList of the Rate represented by the JSON
     */
    public static List<Rate> parseRates(final JSONObject rateInfosJson) {
        return Collections.singletonList(new Rate(rateInfosJson.optJSONObject("RateInfo")));
    }

    /**
     * Constructs a List of Rate objects from a RateInfos JSONObject.
     * @param object The JSONObject which holds a RateInfos field which holds an array of RateInfo objects.
     * @return A fully constructed Rate list.
     */
    public static List<Rate> parseFromRateInfos(final JSONObject object) {
        final String rateInfoId = "RateInfos";
        if (object.optJSONArray(rateInfoId) != null) {
            final JSONArray rateInfos = object.optJSONArray(rateInfoId);
            return parseRates(rateInfos);
        } else if (object.optJSONObject(rateInfoId) != null) {
            final JSONObject rateInfo = object.optJSONObject(rateInfoId);
            return parseRates(rateInfo);
        }
        // if neither of the if/else above, then this was a sabre response that
        // requires ANOTHER call to get the rate information but that is handled
        // by the RoomAvail request, so we do nothing with the rates.
        return Collections.singletonList(null);

    }

    /**
     * Gets the rateKey from a RoomOccupancy which is equivalent to the parameter.
     * @param occupancy A known room occupancy to find a matching RoomOccupancy in this RoomGroup.
     * @return The matching rateKey.
     */
    public String getRateKeyForOccupancy(final RoomOccupancy occupancy) {
        for (Rate.Room room : roomGroup) {
            if (room.occupancy.equals(occupancy)) {
                return room.rateKey;
            }
        }
        return null;
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
         * an unexpected api change or a network failure resulting in invalid json.
         */
        public RateInfo(final JSONObject rate) {

            final String nightlyRatesPerRoom = "NightlyRatesPerRoom";
            final String nightlyRate = "NightlyRate";

            final List<NightlyRate> localNightlyRates = new ArrayList<NightlyRate>();

            if (rate.optJSONArray(nightlyRatesPerRoom) != null) {
                localNightlyRates.addAll(NightlyRate.parseNightlyRates(rate.optJSONArray(nightlyRatesPerRoom)));
            } else if (rate.optJSONObject(nightlyRatesPerRoom) != null) {
                if (rate.optJSONObject(nightlyRatesPerRoom).optJSONArray(nightlyRate) != null) {
                    localNightlyRates.addAll(
                        NightlyRate.parseNightlyRates(
                            rate.optJSONObject(nightlyRatesPerRoom).optJSONArray(nightlyRate)));
                } else {
                    localNightlyRates.addAll(NightlyRate.parseNightlyRates(rate.optJSONObject(nightlyRatesPerRoom)));
                }
            }

            final Map<String, BigDecimal> localSurcharges = new HashMap<String, BigDecimal>();
            if (rate.optJSONArray("Surcharges") != null) {
                final JSONArray jsonSurcharges = rate.optJSONArray("Surcharges");
                for (int i = 0; i < jsonSurcharges.length(); i++) {
                    final JSONObject surchargeJson = jsonSurcharges.optJSONObject(i);
                    localSurcharges.put(
                        surchargeJson.optString("@type"),
                        new BigDecimal(surchargeJson.optString("@amount")));
                }
            } else if (rate.optJSONObject("Surcharges") != null) {
                final JSONObject jsonSurcharge = rate.optJSONObject("Surcharges").optJSONObject("Surcharge");
                localSurcharges.put(
                    jsonSurcharge.optString("@type"),
                    new BigDecimal(jsonSurcharge.optString("@amount")));
            }

            currencyCode = rate.optString("@currencyCode");
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

        /**
         * Gets the total of the surcharges of this Rate.
         * @return The total of the surcharges.
         */
        public BigDecimal getSurchargeTotal() {
            BigDecimal surchargeTotal = BigDecimal.ZERO;
            for (BigDecimal surcharge : surcharges.values()) {
                surchargeTotal = surchargeTotal.add(surcharge);
            }
            return surchargeTotal;
        }

        /**
         * Gets the final total of the rate and taxes together.
         * @return The total cost, including taxes and fees.
         */
        public BigDecimal getTotal() {
            return getRateTotal().add(getSurchargeTotal());
        }
    }

    /**
     * Number of adults, children, and the rate key applied to a particular room.
     */
    public static class Room {
        /**
         * The occupancy supported by this room.
         */
        public final RoomOccupancy occupancy;

        /**
         * The rateKey for this room, used for booking.
         */
        public final String rateKey;

        /**
         * The json-based constructor for this object.
         * @param jsonRoom The JSONObject representing this room.
         */
        public Room(final JSONObject jsonRoom) {
            this.occupancy = new RoomOccupancy(jsonRoom.optInt("numberOfAdults"), jsonRoom.optInt("numberOfChildren"));
            this.rateKey = jsonRoom.optString("rateKey");
        }
    }
}
