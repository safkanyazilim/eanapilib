/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.ean.mobile.HotelRoom;
import com.ean.mobile.exception.DataValidationException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class RoomAvailRequestIntTest {

    @Test
    public void testGetGoodAvail() throws Exception {
        Calendar[] calendars = DateModifier.getAnArrayOfCalendarsWithOffsets(1, 3);
        List<HotelRoom> rooms = RoomAvailRequest.getRoomAvail(106347L, 1, 0, calendars[0], calendars[1], "");
        assertThat(rooms.size(), greaterThan(0));
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailWrongDates() throws Exception {
        Calendar[] calendars = DateModifier.getAnArrayOfCalendarsWithOffsets(1, -3);
        RoomAvailRequest.getRoomAvail(106347L, 1, 0, calendars[0], calendars[1], "");
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailBadHotel() throws Exception {
        Calendar[] calendars = DateModifier.getAnArrayOfCalendarsWithOffsets(1, 3);
        RoomAvailRequest.getRoomAvail(-1L, 1, 0, calendars[0], calendars[1], "");
    }
}
