/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.Calendar;

import org.junit.Test;

import com.ean.mobile.HotelWrangler;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class ListRequestTest {

    @Test
    public void testSearchForHotelsHappy() throws Exception {

        Calendar today = Calendar.getInstance();
        Calendar twoDaysFromNow = Calendar.getInstance();
        twoDaysFromNow.add(Calendar.DAY_OF_YEAR, 2);

        HotelWrangler wrangler = new HotelWrangler();
        wrangler.setArrivalDate(today.getTime());
        wrangler.setDepartureDate(twoDaysFromNow.getTime());
        ListRequest.searchForHotels("rome, it", wrangler);

        assertThat(wrangler.getInfos().size(), greaterThan(0));
    }
}
