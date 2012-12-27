/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.List;

import org.joda.time.DateTime;

import org.junit.Test;

import com.ean.mobile.HotelRoom;
import com.ean.mobile.exception.DataValidationException;
import com.ean.mobile.exception.EanWsError;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RoomAvailRequestIntTest {

    private static final long HOTEL_IN_SEATTLE = 106347L;

    @Test
    public void testGetGoodAvail() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, 3);
        try {
            List<HotelRoom> rooms
                    = RoomAvailRequest.getRoomAvail(HOTEL_IN_SEATTLE, 1, 0, dateTimes[0], dateTimes[1], "");
            assertThat(rooms.size(), greaterThan(0));
        } catch (EanWsError ewe) {
            assertEquals("SOLD_OUT", ewe.category);
        }
    }

    @Test
    public void testMultiRoomAvail() {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, 3);
        try {
            List<HotelRoom> rooms
                    = RoomAvailRequest.getRoomAvail(HOTEL_IN_SEATTLE, 1, 0, dateTimes[0], dateTimes[1], "");
            assertThat(rooms.size(), greaterThan(0));
        } catch (EanWsError ewe) {
            assertEquals("SOLD_OUT", ewe.category);
        }
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailWrongDates() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, -3);
        RoomAvailRequest.getRoomAvail(HOTEL_IN_SEATTLE, 1, 0, dateTimes[0], dateTimes[1], "");
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailBadHotel() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, 3);
        RoomAvailRequest.getRoomAvail(-1L, 1, 0, dateTimes[0], dateTimes[1], "");
    }
}
