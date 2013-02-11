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

import com.ean.mobile.TestConstants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.BaseRequestTest;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.DateModifier;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class RoomAvailabilityRequestTest extends BaseRequestTest {

    private static final long HOTEL_IN_SEATTLE = 106347L;
    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(2, 1);
    private static final LocalDate[] DATES = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

    private RoomAvailabilityRequest roomAvailabilityRequest;

    @Before
    public void setUp() {
        super.setUp();
        CommonParameters.customerSessionId = TestConstants.CUSTOMER_SESSION_ID;
        roomAvailabilityRequest = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, OCCUPANCY,
            DATES[0], DATES[1]);
    }

    @Test
    public void testConsumeNullJson() throws Exception {
        assertNull(roomAvailabilityRequest.consume(null));
    }

    @Test(expected = JSONException.class)
    public void testConsumeEmptyJson() throws Exception {
        roomAvailabilityRequest.consume(new JSONObject());
    }

    @Test(expected = JSONException.class)
    public void testConsumeInvalidJson() throws Exception {
        roomAvailabilityRequest.consume(new JSONObject("{ invalid! }"));
    }

    @Test(expected = EanWsError.class)
    public void testConsumeEanWsError() throws Exception {
        roomAvailabilityRequest.consume(
            new JSONObject("{ HotelRoomAvailabilityResponse: { EanWsError: { category: 'test' } } }"));
    }

    @Test
    public void testConsumeSingleRoom() throws Exception {
        StringBuilder jsonString = new StringBuilder(308);

        jsonString.append("{ HotelRoomAvailabilityResponse: { arrivalDate: '7/15/2013', departureDate: '7/16/2013', ");
        jsonString.append("HotelRoomResponse: { RateInfos: { RateInfo: { RoomGroup: { }, ChargeableRateInfo: ");
        jsonString.append("{ NightlyRatesPerRoom: { NightlyRate: { @rate: '1.00', @baseRate: '2.00' } }, ");
        jsonString.append("Surcharges: { Surcharge: { @amount: '3.00' } } }");
        jsonString.append("} } } } } }");

        List<HotelRoom> hotelRooms = roomAvailabilityRequest.consume(new JSONObject(jsonString.toString()));

        assertNotNull(hotelRooms);
        assertThat(hotelRooms.size(), greaterThan(0));
    }

    @Test
    public void testConsumeMultipleRooms() throws Exception {
        StringBuilder jsonString = new StringBuilder(486);

        jsonString.append("{ HotelRoomAvailabilityResponse: { arrivalDate: '7/15/2013', departureDate: '7/16/2013', ");
        jsonString.append("HotelRoomResponse: [{ 0: { RateInfos: { RateInfo: { ChargeableRateInfo: ");
        jsonString.append("{ NightlyRatesPerRoom: { NightlyRate: { @rate: '1.00', @baseRate: '2.00' } }, ");
        jsonString.append("Surcharges: { Surcharge: { @amount: '3.00' } } }");
        jsonString.append("} } } }, { 1: { RateInfos: { RateInfo: { ChargeableRateInfo: ");
        jsonString.append("{ NightlyRatesPerRoom: { NightlyRate: { @rate: '4.00', @baseRate: '5.00' } }, ");
        jsonString.append("Surcharges: { Surcharge: { @amount: '6.00' } } }");
        jsonString.append("} } } }] } }");

        List<HotelRoom> hotelRooms = roomAvailabilityRequest.consume(new JSONObject(jsonString.toString()));

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
        roomAvailabilityRequest = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, occupancies, DATES[0], DATES[1]);

        doUriAssertions(roomAvailabilityRequest.getUri(), "&room2=2,0");
    }

    @Test
    public void testIsSecure() {
        assertFalse(roomAvailabilityRequest.requiresSecure());
    }

    private static void doUriAssertions(final URI uri, final String suffix) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");

        StringBuilder queryString = buildBaseQueryString();
        queryString.append("&arrivalDate=");
        queryString.append(dateTimeFormatter.print(DATES[0]));
        queryString.append("&departureDate=");
        queryString.append(dateTimeFormatter.print(DATES[1]));
        queryString.append("&hotelId=106347&includeDetails=true&room1=2,0");
        if (suffix != null) {
            queryString.append(suffix);
        }

        assertEquals("http", uri.getScheme());
        assertEquals("api.ean.com", uri.getHost());
        assertEquals("/ean-services/rs/hotel/v3/avail", uri.getPath());
        assertEquals(queryString.toString(), uri.getQuery());
    }
}
