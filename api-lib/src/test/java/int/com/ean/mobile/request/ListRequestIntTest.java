/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.DataValidationException;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ListRequestIntTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    private static final String LOCALE = "en_US";

    private static final String CURRENCY_CODE = "USD";
    
    @Test
    public void testSearchForHotelsHappy() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY,
            dateTimes[0], dateTimes[1], null, LOCALE, CURRENCY_CODE);

        assertThat(results.size(), both(greaterThan(0)).and(lessThanOrEqualTo(results.pageSize)));
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

        assertThat(results.size(), both(greaterThan(0)).and(lessThanOrEqualTo(results.pageSize)));
    }

    @Test
    public void testSearchForHotelsPaging() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", OCCUPANCY,
            dateTimes[0], dateTimes[1], null, LOCALE, CURRENCY_CODE);

        //multiply page size by 4 since we will load 4 "pages"
        assertThat(results.totalNumberOfResults, greaterThanOrEqualTo(results.pageSize * 4));
        assertEquals(results.pageSize, results.size());

        // Paginate a few times and make sure they are ordered correctly.
        results = ListRequest.loadMoreResults(results);
        assertPagination(results);
        results = ListRequest.loadMoreResults(results);
        assertPagination(results);
        results = ListRequest.loadMoreResults(results);
        assertPagination(results);
    }

    private static void assertPagination(final HotelInfoList results) {

        // now we assert that the list has grown expectedly.
        assertEquals(results.pageSize * (results.getCurrentPageIndex() + 1), results.size());

        // now we need to asset that the list has grown in the correct way.
        for (HotelInfo info : results) {
            assertEquals("List out of order.", (long) info.listOrder, (long) results.indexOf(info));
        }
    }
}
