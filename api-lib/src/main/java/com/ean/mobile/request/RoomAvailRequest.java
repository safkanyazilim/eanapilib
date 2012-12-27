/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.HotelRoom;
import com.ean.mobile.RoomOccupancy;
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
     * @param room The singular room occupancy to search for.
     * @param arrivalDate The date of arrival, specified as a DateTime object.
     * @param departureDate The date of departure (from the hotel), specified as a DateTime object.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *                          Can be null.
     * @return The list of HotelRoom objects returned by the API request.
     * @throws IOException If there is a communication issue while getting the response.
     * @throws EanWsError If there is an error in the API that was returned.
     */
    public static List<HotelRoom> getRoomAvail(final long hotelId,
                                               final RoomOccupancy room,
                                               final DateTime arrivalDate,
                                               final DateTime departureDate,
                                               final String customerSessionId)
            throws IOException, EanWsError {
        return getRoomAvail(hotelId, Collections.singletonList(room), arrivalDate, departureDate, customerSessionId);
    }
    /**
     * Gets the room availability for the specified information.
     *
     * THIS SHOULD NOT BE RUN ON THE MAIN THREAD. It is a long-running network process and so might cause
     * force close dialogs.
     *
     * @param hotelId The hotel to search for availability in.
     * @param rooms The list of room occupancies to search for.
     * @param arrivalDate The date of arrival, specified as a DateTime object.
     * @param departureDate The date of departure (from the hotel), specified as a DateTime object.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *                          Can be null.
     * @return The list of HotelRoom objects returned by the API request.
     * @throws IOException If there is a communication issue while getting the response.
     * @throws EanWsError If there is an error in the API that was returned.
     */

    public static List<HotelRoom> getRoomAvail(final long hotelId,
                                               final List<RoomOccupancy> rooms,
                                               final DateTime arrivalDate,
                                               final DateTime departureDate,
                                               final String customerSessionId)
            throws IOException, EanWsError {
        final List<NameValuePair> baseUrlParams = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("cid", CID),
            new BasicNameValuePair("minorRev", MINOR_REV),
            new BasicNameValuePair("apiKey", API_KEY),
            new BasicNameValuePair("locale", LOCALE),
            new BasicNameValuePair("currencyCode", CURRENCY_CODE),
            new BasicNameValuePair("arrivalDate", formatApiDate(arrivalDate)),
            new BasicNameValuePair("departureDate", formatApiDate(departureDate)),
            new BasicNameValuePair("includeDetails", "true"),
            new BasicNameValuePair("customerSessionId", customerSessionId),
            new BasicNameValuePair("hotelId", Long.toString(hotelId))
        );

        final List<NameValuePair> roomPairs = new ArrayList<NameValuePair>(rooms.size());
        for (RoomOccupancy occupancy : rooms) {
            roomPairs.add(new BasicNameValuePair(
                    "room" + roomPairs.size(),
                    formatRoomOccupancy(occupancy.numberOfAdults, occupancy.childAges.size())));
        }

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>(baseUrlParams.size() + roomPairs.size());
        urlParameters.addAll(baseUrlParams);
        urlParameters.addAll(roomPairs);

        try {
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
        } catch (JSONException jse) {
            return Collections.emptyList();
        }
    }
}
