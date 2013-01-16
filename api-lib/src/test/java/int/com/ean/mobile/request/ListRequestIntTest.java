/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.DataValidationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ListRequestIntTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    private static final String LOCALE = "en_US";

    private static final String CURRENCY_CODE = "USD";
    
    @Test
    public void testSearchForHotelsHappy() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY,
            dateTimes[0], dateTimes[1], null, LOCALE, CURRENCY_CODE);

        assertEquals(10, results.hotelInfos.size());
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, -3);

        ListRequest.searchForHotels("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1], null, LOCALE, CURRENCY_CODE);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest.searchForHotels(
                "sea of tranquility, moon",
                OCCUPANCY,
                dateTimes[0],
                dateTimes[1],
                null,
                LOCALE,
                CURRENCY_CODE);
    }

    @Test
    public void testSearchForHotelsMultiRoomType() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        List<RoomOccupancy> occupancies = Arrays.asList(
                OCCUPANCY,
                new RoomOccupancy(1, 3)
        );

        HotelInfoList results = ListRequest.searchForHotels(
                "rome, it",
                occupancies,
                dateTimes[0],
                dateTimes[1],
                null,
                LOCALE,
                CURRENCY_CODE);

        assertEquals(10, results.hotelInfos.size());
    }

    @Test
    public void testSearchForHotelsPaging() throws Exception {
        Set<Long> hotelIdsReturned = new HashSet<Long>();
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY,
            dateTimes[0], dateTimes[1], null, LOCALE, CURRENCY_CODE);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        // Paginate a few times and make sure they are ordered correctly.
        results = ListRequest.loadMoreResults(LOCALE, CURRENCY_CODE,
            results.cacheKey, results.cacheLocation, results.customerSessionId);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        results = ListRequest.loadMoreResults(LOCALE, CURRENCY_CODE,
            results.cacheKey, results.cacheLocation, results.customerSessionId);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        results = ListRequest.loadMoreResults(LOCALE, CURRENCY_CODE,
            results.cacheKey, results.cacheLocation, results.customerSessionId);
        checkForDuplicateHotelId(hotelIdsReturned, results);
    }

    private void checkForDuplicateHotelId(final Set<Long> hotelIdsReturned, final HotelInfoList results) {
        for (HotelInfo hotelInfo : results.hotelInfos) {
            assertFalse(hotelIdsReturned.contains(hotelInfo.hotelId));
            hotelIdsReturned.add(hotelInfo.hotelId);
        }
    }

}
