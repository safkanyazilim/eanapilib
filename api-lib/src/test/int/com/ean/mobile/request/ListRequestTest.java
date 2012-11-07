/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.Calendar;

import org.junit.Test;

import com.ean.mobile.exception.DataValidationException;
import com.ean.mobile.HotelWrangler;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class ListRequestTest {

    @Test
    public void testSearchForHotelsHappy() throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar threeDaysFromNow = Calendar.getInstance();
        threeDaysFromNow.add(Calendar.DAY_OF_YEAR, 3);

        HotelWrangler wrangler = new HotelWrangler();
        wrangler.setArrivalDate(tomorrow.getTime());
        wrangler.setDepartureDate(threeDaysFromNow.getTime());
        ListRequest.searchForHotels("rome, it", wrangler);

        assertThat(wrangler.getInfos().size(), greaterThan(0));
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar threeDaysAgo = Calendar.getInstance();
        threeDaysAgo.add(Calendar.DAY_OF_YEAR, -3);

        HotelWrangler wrangler = new HotelWrangler();
        wrangler.setArrivalDate(tomorrow.getTime());
        wrangler.setDepartureDate(threeDaysAgo.getTime());
        ListRequest.searchForHotels("rome, it", wrangler);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar threeDaysFromNow = Calendar.getInstance();
        threeDaysFromNow.add(Calendar.DAY_OF_YEAR, 3);

        HotelWrangler wrangler = new HotelWrangler();
        wrangler.setArrivalDate(tomorrow.getTime());
        wrangler.setDepartureDate(threeDaysFromNow.getTime());
        ListRequest.searchForHotels("sea of tranquility, moon", wrangler);
    }
}
