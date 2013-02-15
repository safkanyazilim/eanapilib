/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel.request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.ean.mobile.exception.DataValidationException;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelList;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.DateModifier;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.request.RequestTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ListRequestIntTest extends RequestTestBase {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Test
    public void testSearchForHotelsHappy() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest listRequest = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        HotelList results = RequestProcessor.run(listRequest);

        assertEquals(10, results.hotels.size());
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, -3);

        ListRequest listRequest
            = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        RequestProcessor.run(listRequest);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest listRequest = new ListRequest(
            "sea of tranquility, moon", OCCUPANCY, dateTimes[0], dateTimes[1]);

        RequestProcessor.run(listRequest);
    }

    @Test
    public void testSearchForHotelsMultiRoomType() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        List<RoomOccupancy> occupancies = Arrays.asList(OCCUPANCY, new RoomOccupancy(1, 3));

        ListRequest listRequest = new ListRequest("rome, it", occupancies, dateTimes[0], dateTimes[1]);

        HotelList results = RequestProcessor.run(listRequest);

        assertEquals(10, results.hotels.size());
    }

    @Test
    public void testSearchForHotelsPaging() throws Exception {
        Set<Long> hotelIdsReturned = new HashSet<Long>();
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest listRequest = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);
        HotelList results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        // Paginate a few times and make sure they are ordered correctly.
        listRequest = new ListRequest(results.cacheKey, results.cacheLocation);
        results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        listRequest = new ListRequest(results.cacheKey, results.cacheLocation);
        results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        listRequest = new ListRequest(results.cacheKey, results.cacheLocation);
        results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);
    }

    private void checkForDuplicateHotelId(final Set<Long> hotelIdsReturned, final HotelList results) {
        for (Hotel hotel : results.hotels) {
            assertFalse(hotelIdsReturned.contains(hotel.hotelId));
            hotelIdsReturned.add(hotel.hotelId);
        }
    }

}
