/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;

import static org.junit.Assert.assertNull;

public class ListRequestTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Test
    public void testConsumeNull() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);
        ListRequest listRequest
            = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1], null, "en_US", "USD");

        HotelInfoList hotelInfoList = listRequest.consume(null);

        assertNull(hotelInfoList);
    }
}
