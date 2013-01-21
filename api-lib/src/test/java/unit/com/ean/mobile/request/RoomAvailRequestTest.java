/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.net.URI;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.RoomOccupancy;

import static org.junit.Assert.assertEquals;

public class RoomAvailRequestTest {

    private static final long HOTEL_IN_SEATTLE = 106347L;
    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    RoomAvailRequest roomAvailRequest;

    @Before
    public void setUp() {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        roomAvailRequest = new RoomAvailRequest(HOTEL_IN_SEATTLE, OCCUPANCY,
            dateTimes[0], dateTimes[1], "", "en_US", "USD");
    }

    @Test(expected = JSONException.class)
    public void testConsumeEmptyJson() throws Exception {
        roomAvailRequest.consume(new JSONObject());
    }

    @Test
    public void testConsumeValidJson() throws Exception {
//        StringBuilder jsonString = new StringBuilder();

        // TODO: build valid JSON

//        List<HotelRoom> hotelRooms = roomAvailRequest.consume(new JSONObject(jsonString.toString()));
//
//        assertNotNull(hotelRooms);
//        assertThat(hotelRooms, is(not(empty())));
    }

    @Test
    public void testGetUriWithQueryString() throws Exception {
        final URI uri = roomAvailRequest.getUri();
        doUriAssertions(uri);

        // TODO: fix this test

//        assertEquals("", uri.getQuery());
    }

    @Test
    public void testGetUriNoQueryString() throws Exception {
        // TODO: fix this test
//        roomAvailRequest = new RoomAvailRequest(null);
        final URI uri = roomAvailRequest.getUri();
        doUriAssertions(uri);
//        assertNull(uri.getQuery());
    }

    @Test
    public void testIsSecure() {
        // TODO: fix this test
//        assertFalse(new RoomAvailRequest(null).isSecure());
    }

    private static void doUriAssertions(final URI uri) {
        assertEquals("http", uri.getScheme());
        assertEquals("api.ean.com", uri.getHost());
        assertEquals("/ean-services/rs/hotel/v3/avail", uri.getPath());
    }
}
