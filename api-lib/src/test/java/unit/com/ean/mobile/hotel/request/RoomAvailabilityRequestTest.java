/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
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
import com.ean.mobile.TestConstants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.DateModifier;
import com.ean.mobile.request.RequestTestBase;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class RoomAvailabilityRequestTest extends RequestTestBase {

    private static final long HOTEL_IN_SEATTLE = 106347L;
    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(2, Arrays.asList(5));
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
        List<RoomOccupancy> occupancies = Arrays.asList(OCCUPANCY, new RoomOccupancy(2, Arrays.asList(4)));
        roomAvailabilityRequest
            = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, occupancies, DATES[0], DATES[1]);
        doUriAssertions(roomAvailabilityRequest.getUri(), "&room2=2,4");
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
        queryString.append("&hotelId=106347&includeDetails=true&room1=2,5");
        if (suffix != null) {
            queryString.append(suffix);
        }

        assertEquals("http", uri.getScheme());
        assertEquals("api.ean.com", uri.getHost());
        assertEquals("/ean-services/rs/hotel/v3/avail", uri.getPath());
        assertEquals(queryString.toString(), uri.getQuery());
    }
}
