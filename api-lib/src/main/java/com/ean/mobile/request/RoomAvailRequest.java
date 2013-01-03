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

import org.joda.time.LocalDate;
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
     * @param arrivalDate The date of arrival.
     * @param departureDate The date of departure (from the hotel).
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *                          Can be null.
     * @return The list of HotelRoom objects returned by the API request.
     * @throws IOException If there is a communication issue while getting the response.
     * @throws EanWsError If there is an error in the API that was returned.
     */
    public static List<HotelRoom> getRoomAvail(final long hotelId,
                                               final RoomOccupancy room,
                                               final LocalDate arrivalDate,
                                               final LocalDate departureDate,
                                               final String customerSessionId,
                                               final String locale,
                                               final String currencyCode)
            throws IOException, EanWsError {
        return getRoomAvail(
                hotelId,
                Collections.singletonList(room),
                arrivalDate,
                departureDate,
                customerSessionId,
                locale,
                currencyCode);
    }
    /**
     * Gets the room availability for the specified information.
     *
     * THIS SHOULD NOT BE RUN ON THE MAIN THREAD. It is a long-running network process and so might cause
     * force close dialogs otherwise.
     *
     * @param hotelId The hotel to search for availability in.
     * @param rooms The list of room occupancies to search for.
     * @param arrivalDate The date of arrival.
     * @param departureDate The date of departure (from the hotel).
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *                          The same customerSessionId as returned to
     *                          {@link com.ean.mobile.HotelInfoList#customerSessionId}.
     * @return The list of HotelRoom objects returned by the API request.
     * @throws IOException If there is a communication issue while getting the response.
     * @throws EanWsError If there is an error in the API that was returned.
     */

    public static List<HotelRoom> getRoomAvail(final long hotelId,
                                               final List<RoomOccupancy> rooms,
                                               final LocalDate arrivalDate,
                                               final LocalDate departureDate,
                                               final String customerSessionId,
                                               final String locale,
                                               final String currencyCode)
            throws IOException, EanWsError {
        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("customerSessionId", customerSessionId),
                new BasicNameValuePair("hotelId", Long.toString(hotelId)),
                new BasicNameValuePair("includeDetails", "true")
        );

        final List<NameValuePair> roomPairs = new ArrayList<NameValuePair>(rooms.size());
        for (RoomOccupancy occupancy : rooms) {
            roomPairs.add(new BasicNameValuePair(
                    "room" + (roomPairs.size() + 1),
                    occupancy.asAbbreviatedRequestString()));
        }

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters(locale, currencyCode, arrivalDate, departureDate));
        urlParameters.addAll(requestParameters);
        urlParameters.addAll(roomPairs);

        try {
            final JSONObject response
                    = performApiRequest(URL_SUBDIR, urlParameters).optJSONObject("HotelRoomAvailabilityResponse");
            // TODO: handler EanWsError objects, such as sold out rooms
            if (response.has("EanWsError")) {
                throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
            }

            final List<HotelRoom> hotelRooms;
            if (response.has("HotelRoomResponse")) {
                // we know that it has HotelRoomResponse, just don't know if it'll be
                // parsed as an object or as an array. If there's only one in the collection,
                // it'll be parsed as a singular object, otherwise it'll be an array.
                if (response.optJSONArray("HotelRoomResponse") != null) {
                    final JSONArray hotelRoomResponse = response.optJSONArray("HotelRoomResponse");
                    hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse, arrivalDate);
                } else {
                    final JSONObject hotelRoomResponse = response.optJSONObject("HotelRoomResponse");
                    hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse, arrivalDate);
                }
            } else if (response.has("EanWsError")) {
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
