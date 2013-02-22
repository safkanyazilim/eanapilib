/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel.request;

import java.net.URI;
import java.net.URISyntaxException;
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

import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.Request;

/**
 * The class to use to get specific availability of rooms for a particular hotel, occupancy, and occupancy dates.
 */
public final class RoomAvailabilityRequest extends Request<List<HotelRoom>> {

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
     */
    public RoomAvailabilityRequest(final long hotelId, final RoomOccupancy room,
            final LocalDate arrivalDate, final LocalDate departureDate) {

        this(hotelId, Collections.singletonList(room), arrivalDate, departureDate);
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
     */

    public RoomAvailabilityRequest(final long hotelId, final List<RoomOccupancy> rooms,
            final LocalDate arrivalDate, final LocalDate departureDate) {

        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("hotelId", Long.toString(hotelId)),
            new BasicNameValuePair("includeDetails", "true")
        );

        final List<NameValuePair> roomPairs = new ArrayList<NameValuePair>(rooms.size());
        for (RoomOccupancy occupancy : rooms) {
            roomPairs.add(new BasicNameValuePair("room" + (roomPairs.size() + 1),
                occupancy.asAbbreviatedRequestString()));
        }

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters(arrivalDate, departureDate));
        urlParameters.addAll(requestParameters);
        urlParameters.addAll(roomPairs);

        setUrlParameters(urlParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HotelRoom> consume(final JSONObject jsonObject) throws JSONException, EanWsError {
        if (jsonObject == null) {
            return null;
        }

        final JSONObject response = jsonObject.getJSONObject("HotelRoomAvailabilityResponse");

        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }

        CommonParameters.customerSessionId = response.optString("customerSessionId");

        final List<HotelRoom> hotelRooms;
        if (response.has("HotelRoomResponse")) {
            // we know that it has HotelRoomResponse, just don't know if it'll be
            // parsed as an object or as an array. If there's only one in the collection,
            // it'll be parsed as a singular object, otherwise it'll be an array.
            final LocalDate arrivalDate = LocalDate.parse(response.getString("arrivalDate"), DATE_TIME_FORMATTER);
            if (response.optJSONArray("HotelRoomResponse") != null) {
                final JSONArray hotelRoomResponse = response.optJSONArray("HotelRoomResponse");
                hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse, arrivalDate);
            } else {
                final JSONObject hotelRoomResponse = response.optJSONObject("HotelRoomResponse");
                hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse, arrivalDate);
            }
        } else {
            hotelRooms = Collections.emptyList();
        }

        return hotelRooms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUri() throws URISyntaxException {
        return new URI("http", "api.ean.com", "/ean-services/rs/hotel/v3/avail", getQueryString(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresSecure() {
        return false;
    }
}
