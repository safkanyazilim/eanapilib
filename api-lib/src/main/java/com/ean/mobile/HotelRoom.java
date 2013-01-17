/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The data holder for information about a particular hotel room.
 */
public final class HotelRoom {

    /**
     * The description of the room.
     */
    public final String description;

    /**
     * The description of the promo, if applicable.
     */
    public final String promoDescription;

    /**
     * The rate code for the room. Used as part of the booking process.
     */
    public final String rateCode;

    /**
     * The room type code. Also used as part of the booking process.
     */
    public final String roomTypeCode;

    /**
     * The string representing the smoking preference allowed in this room.
     */
    public final String smokingPreference;

    /**
     * The bedTypeId to be used to book this room.
     */
    public final List<BedType> bedTypes;

    /**
     * The all of the information that is available about the rates charged..
     */
    public final Rate rate;

    /**
     * The cancellation policy associated with this room.
     */
    public final CancellationPolicy cancellationPolicy;

    /**
     * The main constructor that creates HotelRooms from JSONObjects.
     * @param roomRateDetail The JSON information about this hotel room.
     * @param arrivalDate The arrival date of the room. Used to calculate the cancellation policy.
     */
    public HotelRoom(final JSONObject roomRateDetail, final LocalDate arrivalDate) {

        this.description = roomRateDetail.optString("roomTypeDescription");
        this.rateCode = roomRateDetail.optString("rateCode");
        this.roomTypeCode = roomRateDetail.optString("roomTypeCode");
        this.promoDescription = roomRateDetail.optString("promoDescription");
        this.smokingPreference = roomRateDetail.optString("smokingPreferences");
        this.bedTypes = extractBedTypesFromJsonObject(roomRateDetail);
        this.rate = Rate.parseFromRateInfos(roomRateDetail).get(0);
        this.cancellationPolicy = new CancellationPolicy(roomRateDetail, arrivalDate);
    }

    private List<BedType> extractBedTypesFromJsonObject(JSONObject roomRateDetail) {
        if (roomRateDetail.optJSONObject("BedTypes") != null) {
            if (roomRateDetail.optJSONObject("BedTypes").optJSONArray("BedType") != null) {
                return BedType.fromJson(roomRateDetail.optJSONObject("BedTypes").optJSONArray("BedType"));
            } else if (roomRateDetail.optJSONObject("BedTypes").optJSONObject("BedType") != null) {
                return BedType.fromJson(roomRateDetail.optJSONObject("BedTypes").optJSONObject("BedType"));
            }
        }
        return Collections.emptyList();
    }

    /**
     * Parses a list of HotelRoom objects from a JSONArray of hotel room objects.
     * @param hotelRoomResponseJson The JSONArray from which to parse HotelRoom objects.
     * @param arrivalDate The arrival date of the room. Used to calculate the cancellation policy.
     * @return The newly formed HotelRoom objects
     */
    public static List<HotelRoom> parseRoomRateDetails(final JSONArray hotelRoomResponseJson,
                                                       final LocalDate arrivalDate) {
        final List<HotelRoom> hotelRooms = new ArrayList<HotelRoom>(hotelRoomResponseJson.length());
        for (int j = 0; j < hotelRoomResponseJson.length(); j++) {
            hotelRooms.add(new HotelRoom(hotelRoomResponseJson.optJSONObject(j), arrivalDate));
        }

        return hotelRooms;
    }

    /**
     * Creates a singleton list of HotelRoom from a JSONObject.
     * @param hotelRoomResponseJson The JSONObject from which to parse the HotelRoom Object.
     * @param arrivalDate The arrival date of the room. Used to calculate the cancellation policy.
     * @return The newly formed HotelRoom object
     */
    public static List<HotelRoom> parseRoomRateDetails(final JSONObject hotelRoomResponseJson,
            final LocalDate arrivalDate) {
        return Collections.singletonList(new HotelRoom(hotelRoomResponseJson, arrivalDate));
    }

    /**
     * Gets the total of all of the base rates for this room.
     * @return The base total.
     */
    public BigDecimal getTotalBaseRate() {
        return rate.chargeable.getBaseRateTotal();
    }

    /**
     * Gets the total of all of the rates for this room, including taxes and fees.
     * @return The net total.
     */
    public BigDecimal getTotalRate() {
        return rate.chargeable.getRateTotal();
    }

    /**
     * Gets the taxes/fees portion of the total rate.
     * @return Taxes and fees.
     */
    public BigDecimal getTaxesAndFees() {
        // TODO: Make this actually get the taxes and fees.
        return BigDecimal.ZERO;
    }

    /**
     * The type of beds in the room. Two queens, single king, etc.
     */
    public static final class BedType {
        /**
         * The numeric id of the type of beds in the room.
         */
        public final String id;

        /**
         * The description of the beds, such as two queens, etc.
         */
        public final String description;

        /**
         * The standard constructor. Sets up the two fields of the class.
         * @param id see {@link BedType#id}.
         * @param description see {@link BedType#description}.
         */
        public BedType(final String id, final String description) {
            this.id = id;
            this.description = description;
        }

        /**
         * Builds a list of BedTypes from a singular JSONObject. Will be a singleton list.
         * @param bedTypeJson The JSONObject used to construct the singular list.
         * @return The singleton list constructed from the JSONObject.
         */
        public static List<BedType> fromJson(final JSONObject bedTypeJson) {
            return Collections.unmodifiableList(Collections.singletonList(
                new BedType(bedTypeJson.optString("@id"), bedTypeJson.optString("description"))));
        }

        /**
         * Builds a list of BedTypes from a JSONArray of objects. Will have the same length as the json array
         * @param bedTypesJson The array of bed types.
         * @return The new list of bed types.
         */
        public static List<BedType> fromJson(final JSONArray bedTypesJson) {
            final List<BedType> bedTypes = new ArrayList<BedType>(bedTypesJson.length());
            JSONObject bedTypeJson;
            for (int i = 0; i < bedTypesJson.length(); i++) {
                bedTypeJson = bedTypesJson.optJSONObject(i);
                bedTypes.add(new BedType(bedTypeJson.optString("@id"), bedTypeJson.optString("description")));
            }
            return Collections.unmodifiableList(bedTypes);
        }
    }

}
