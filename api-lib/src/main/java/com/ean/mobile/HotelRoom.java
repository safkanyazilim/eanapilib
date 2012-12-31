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
     * The main constructor that creates HotelRooms from JSONObjects.
     * @param roomRateDetail The JSON information about this hotel room.
     * @throws JSONException If any of the fields required do not exist.
     */
    public HotelRoom(final JSONObject roomRateDetail) throws JSONException {

        this.description = roomRateDetail.optString("roomTypeDescription");
        this.rateCode = roomRateDetail.optString("rateCode");
        this.roomTypeCode = roomRateDetail.optString("roomTypeCode");
        this.promoDescription = roomRateDetail.optString("promoDescription");
        this.smokingPreference = roomRateDetail.optString("smokingPreferences");
        if (roomRateDetail.optJSONObject("BedTypes").optJSONArray("BedType") != null) {
            this.bedTypes = BedType.fromJson(roomRateDetail.optJSONObject("BedTypes").optJSONArray("BedType"));
        } else if (roomRateDetail.optJSONObject("BedTypes").optJSONObject("BedType") != null) {
            this.bedTypes = BedType.fromJson(roomRateDetail.optJSONObject("BedTypes").optJSONObject("BedType"));
        } else {
            this.bedTypes = Collections.emptyList();
        }
        this.rate = Rate.parseFromRateInfos(roomRateDetail).get(0);
    }

    /**
     * Parses a list of HotelRoom objects from a JSONArray of hotel room objects.
     * @param hotelRoomResponseJson The JSONArray from which to parse HotelRoom objects.
     * @return The newly formed HotelRoom objects
     * @throws JSONException If there is an error in the JSON.
     */
    public static List<HotelRoom> parseRoomRateDetails(final JSONArray hotelRoomResponseJson) throws JSONException {
        final List<HotelRoom> hotelRooms = new ArrayList<HotelRoom>(hotelRoomResponseJson.length());
        for (int j = 0; j < hotelRoomResponseJson.length(); j++) {
            hotelRooms.add(new HotelRoom(hotelRoomResponseJson.getJSONObject(j)));
        }

        return hotelRooms;
    }

    /**
     * Creates a singleton list of HotelRoom from a JSONObject.
     * @param hotelRoomResponseJson The JSONObject from which to parse the HotelRoom Object.
     * @return The newly formed HotelRoom object
     * @throws JSONException If there is an error in the JSON.
     */
    public static List<HotelRoom> parseRoomRateDetails(final JSONObject hotelRoomResponseJson) throws JSONException {
        return Collections.singletonList(new HotelRoom(hotelRoomResponseJson));
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

    public static final class BedType {
        public final String id;
        public final String description;

        public BedType(final String id, final String description) {
            this.id = id;
            this.description = description;
        }

        public static List<BedType> fromJson(JSONObject bedTypeJson) {
            return Collections.singletonList(
                    new BedType(bedTypeJson.optString("@id"), bedTypeJson.optString("description")));
        }

        public static List<BedType> fromJson(JSONArray bedTypesJson) {
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
