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
    public final String bedTypeId;

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

        this.description = roomRateDetail.optString("roomTypeDescription", "");
        this.rateCode = roomRateDetail.optString("rateCode", "");
        this.roomTypeCode = roomRateDetail.optString("roomTypeCode");
        this.promoDescription = roomRateDetail.optString("promoDescription");
        this.smokingPreference = roomRateDetail.optString("smokingPreference");
        this.bedTypeId = roomRateDetail.optString("bedTypeId");
        final String rateInfoId = "RateInfos";
        if (roomRateDetail.optJSONArray(rateInfoId) != null) {
            final JSONArray rateInfos = roomRateDetail.getJSONArray(rateInfoId);
            this.rate = Rate.parseRates(rateInfos).get(0);
        } else if (roomRateDetail.optJSONObject(rateInfoId) != null) {
            final JSONObject rateInfo = roomRateDetail.getJSONObject(rateInfoId);
            this.rate = Rate.parseRates(rateInfo).get(0);
        } else {
            // if neither of the if/else above, then this was a sabre response that
            // requires ANOTHER call to get the rate information but that is handled
            // by the RoomAvail request, so we do nothing with the rates.
            this.rate = null;
        }
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
}
