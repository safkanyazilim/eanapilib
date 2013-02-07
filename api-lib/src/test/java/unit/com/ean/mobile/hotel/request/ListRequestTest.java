/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel.request;

import java.util.Locale;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.BaseRequest;
import com.ean.mobile.hotel.HotelList;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.DateModifier;

import static org.junit.Assert.assertNull;

public class ListRequestTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Before
    public void setUp() {
        BaseRequest.initialize("55505", "cbrzfta369qwyrm9t5b8y8kf", Locale.US.toString(), "USD");
    }

    @Test
    public void testConsumeNull() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);
        ListRequest listRequest = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1], null);

        HotelList hotelList = listRequest.consume(null);

        assertNull(hotelList);
    }
}
