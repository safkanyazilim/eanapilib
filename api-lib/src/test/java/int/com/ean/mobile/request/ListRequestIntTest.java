/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import org.apache.http.NameValuePair;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.DataValidationException;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Request.class)
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

    @Test
    public void testSearchForHotelsNullResponse() throws Exception {
        mockStatic(Request.class);
        when(Request.performApiRequest(anyString(), anyListOf(NameValuePair.class))).thenThrow(new JSONException(""));

        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        assertNotNull(results);
        assertEquals(0, results.size());
        assertNull(results.customerSessionId);
        assertNull(results.cacheKey);
        assertNull(results.cacheLocation);
    }
}
