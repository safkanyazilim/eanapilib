/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.HotelRoom;
import com.ean.mobile.exception.EanWsError;

/**
 * The class to use to get specific availability of rooms for a particular hotel, occupancy, and occupancy dates.
 */
public final class RoomAvailRequest extends Request {
    private static final String URL_SUBDIR = "avail";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private RoomAvailRequest() {
        //see javadoc.
    }

    /**
     * Gets the room availability for the specified information.
     *
     * THIS SHOULD NOT BE RUN ON THE MAIN THREAD. It is a long-running network process and so might cause
     * force close dialogs.
     *
     * @param hotelId The hotel to search for availability in.
     * @param numberOfAdults The number of adults to search for.
     * @param numberOfChildren The number of children to search for.
     * @param arrivalDate The date of arrival, specified as a Calendar object.
     * @param departureDate The date of departure (from the hotel), specified as a Calendar object.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *                          Can be null.
     * @return The list of HotelRoom objects returned by the API request.
     * @throws IOException If there is a communication issue while getting the response.
     * @throws JSONException If the json returned is malformed somehow.
     * @throws EanWsError If there is an error in the API that was returned.
     */
    public static List<HotelRoom> getRoomAvail(final long hotelId,
                                               final int numberOfAdults,
                                               final int numberOfChildren,
                                               final Calendar arrivalDate,
                                               final Calendar departureDate,
                                               final String customerSessionId)
            throws IOException, JSONException, EanWsError {
        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("cid", CID),
            new BasicNameValuePair("minorRev", MINOR_REV),
            new BasicNameValuePair("apiKey", API_KEY),
            new BasicNameValuePair("locale", LOCALE),
            new BasicNameValuePair("currencyCode", CURRENCY_CODE),
            new BasicNameValuePair("arrivalDate", formatApiDate(arrivalDate)),
            new BasicNameValuePair("departureDate", formatApiDate(departureDate)),
            new BasicNameValuePair("includeDetails", "true"),
            new BasicNameValuePair("customerSessionId", customerSessionId),
            new BasicNameValuePair("hotelId", Long.toString(hotelId)),
            new BasicNameValuePair("room1", formatRoomOccupancy(numberOfAdults, numberOfChildren))
        );

        final JSONObject json = performApiRequest(URL_SUBDIR, urlParameters);
        // TODO: handler EanWsError objects, such as sold out rooms
        if (json.has("EanWsError")) {
            throw EanWsError.fromJson(json.getJSONObject("EanWsError"));
        }
        final JSONObject response = json.optJSONObject("HotelRoomAvailabilityResponse");

        final List<HotelRoom> hotelRooms;
        if (response != null && response.has("HotelRoomResponse")) {
            // we know that it has HotelRoomResponse, just don't know if it'll be
            // parsed as an object or as an array. If there's only one in the collection,
            // it'll be parsed as a singular object, otherwise it'll be an array.
            if (response.optJSONArray("HotelRoomResponse") != null) {
                final JSONArray hotelRoomResponse = response.optJSONArray("HotelRoomResponse");
                hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse);
            } else {
                final JSONObject hotelRoomResponse = response.optJSONObject("HotelRoomResponse");
                hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse);
            }
        } else if (response != null && response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        } else {
            hotelRooms = Collections.emptyList();
        }

        return hotelRooms;
    }
}
