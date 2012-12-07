/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.Calendar;

import org.junit.Test;

import com.ean.mobile.HotelInfoList;
import com.ean.mobile.exception.DataValidationException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class ListRequestIntTest {

    @Test
    public void testSearchForHotelsHappy() throws Exception {
        Calendar[] calendars = DateModifier.getAnArrayOfCalendarsWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", 1, 0, calendars[0], calendars[1]);

        assertThat(results.size(), greaterThan(0));
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        Calendar[] calendars = DateModifier.getAnArrayOfCalendarsWithOffsets(1, -3);

        ListRequest.searchForHotels("rome, it", 1, 0, calendars[0], calendars[1]);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        Calendar[] calendars = DateModifier.getAnArrayOfCalendarsWithOffsets(1, 3);

        ListRequest.searchForHotels("sea of tranquility, moon", 1, 0, calendars[0], calendars[1]);
    }
}
