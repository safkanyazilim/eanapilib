/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel.request;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.JSONFileUtil;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.DateModifier;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class RoomAvailabilityRequestTest {

    private static final long HOTEL_IN_SEATTLE = 106347L;
    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(2, 1);
    private static final LocalDate[] DATES = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

    private RoomAvailabilityRequest roomAvailabilityRequest;

    @Before
    public void setUp() {
        roomAvailabilityRequest = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, OCCUPANCY,
            DATES[0], DATES[1], "", "en_US", "USD");
    }

    @Test
    public void testConsumeNullJson() throws Exception {
        assertNull(roomAvailabilityRequest.consume(null));
    }

    @Test(expected = JSONException.class)
    public void testConsumeEmptyJson() throws Exception {
        roomAvailabilityRequest.consume(new JSONObject());
    }

    @Test
    public void testConsumeInvalidJson() throws Exception {
        List<HotelRoom> hotelRooms
            = roomAvailabilityRequest.consume(JSONFileUtil.loadJsonFromFile("invalid-avail.json"));

        assertNotNull(hotelRooms);
        assertThat(hotelRooms, empty());
    }

    @Test(expected = EanWsError.class)
    public void testConsumeEanWsError() throws Exception {
        roomAvailabilityRequest.consume(JSONFileUtil.loadJsonFromFile("error-avail.json"));
    }

    @Test
    public void testConsumeSingleRoom() throws Exception {
        List<HotelRoom> hotelRooms
            = roomAvailabilityRequest.consume(JSONFileUtil.loadJsonFromFile("valid-avail-singleroom.json"));

        assertNotNull(hotelRooms);
        assertThat(hotelRooms.size(), greaterThan(0));
    }

    @Test
    public void testConsumeMultipleRooms() throws Exception {
        List<HotelRoom> hotelRooms
            = roomAvailabilityRequest.consume(JSONFileUtil.loadJsonFromFile("valid-avail-multiroom.json"));

        assertNotNull(hotelRooms);
        assertThat(hotelRooms.size(), greaterThan(0));
    }

    @Test
    public void testGetUriSingleRoom() throws Exception {
        doUriAssertions(roomAvailabilityRequest.getUri(), null);
    }

    @Test
    public void testGetUriMultipleRooms() throws Exception {
        List<RoomOccupancy> occupancies = Arrays.asList(OCCUPANCY, new RoomOccupancy(2, 1));
        roomAvailabilityRequest
            = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, occupancies, DATES[0], DATES[1], "", "en_US", "USD");

        doUriAssertions(roomAvailabilityRequest.getUri(), "&room2=2,0");
    }

    @Test
    public void testIsSecure() {
        assertFalse(roomAvailabilityRequest.requiresSecure());
    }

    private static void doUriAssertions(final URI uri, final String suffix) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");

        StringBuilder queryString = new StringBuilder(222);
        queryString.append("cid=55505&apiKey=cbrzfta369qwyrm9t5b8y8kf&minorRev=20&customerUserAgent=Android");
        queryString.append("&locale=en_US&currencyCode=USD&arrivalDate=");
        queryString.append(dateTimeFormatter.print(DATES[0]));
        queryString.append("&departureDate=");
        queryString.append(dateTimeFormatter.print(DATES[1]));
        queryString.append("&customerSessionId=&hotelId=106347&includeDetails=true&room1=2,0");
        if (suffix != null) {
            queryString.append(suffix);
        }

        assertEquals("http", uri.getScheme());
        assertEquals("api.ean.com", uri.getHost());
        assertEquals("/ean-services/rs/hotel/v3/avail", uri.getPath());
        assertEquals(queryString.toString(), uri.getQuery());
    }
}
