/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import org.apache.http.NameValuePair;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Request.class)
public class ListRequestTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Test
    public void testSearchForHotelsNullResponse() throws Exception {
        mockStatic(Request.class);
        when(Request.performApiRequest(anyString(), anyListOf(NameValuePair.class))).thenThrow(new JSONException(""));

        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        assertNotNull(results);
        assertEquals(0, results.size());
        assertNull(results.customerSessionId);
        assertNull(results.cacheKey);
        assertNull(results.cacheLocation);
    }
}
