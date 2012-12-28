/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import org.joda.time.DateTime;
import org.junit.Test;

import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.DataValidationException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class ListRequestIntTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Test
    public void testSearchForHotelsHappy() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        assertThat(results.size(), greaterThan(0));
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, -3);

        ListRequest.searchForHotels("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, 3);

        ListRequest.searchForHotels("sea of tranquility, moon", OCCUPANCY, dateTimes[0], dateTimes[1]);
    }
}
