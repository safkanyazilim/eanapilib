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

public class ListRequestTest {

    @Test
    public void testSearchForHotelsHappy() throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar threeDaysFromNow = Calendar.getInstance();
        threeDaysFromNow.add(Calendar.DAY_OF_YEAR, 3);

        HotelInfoList results = ListRequest.searchForHotels("rome, it", 1, 0, tomorrow, threeDaysFromNow);

        assertThat(results.size(), greaterThan(0));
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar threeDaysAgo = Calendar.getInstance();
        threeDaysAgo.add(Calendar.DAY_OF_YEAR, -3);

        ListRequest.searchForHotels("rome, it", 1, 0, tomorrow, threeDaysAgo);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar threeDaysFromNow = Calendar.getInstance();
        threeDaysFromNow.add(Calendar.DAY_OF_YEAR, 3);

        ListRequest.searchForHotels("sea of tranquility, moon", 1, 0, tomorrow, threeDaysFromNow);
    }
}
