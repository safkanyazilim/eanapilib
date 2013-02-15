/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel.request;

import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.ConfirmationStatus;
import com.ean.mobile.hotel.Itinerary;
import com.ean.mobile.hotel.Reservation;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.request.RequestTestBase;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ItineraryRequestIntTest extends RequestTestBase {

    private static final String EMAIL = "test@expedia.com";

    @Test(expected = EanWsError.class)
    public void testItineraryLookupInvalidItineraryIdAndEmail() throws Exception {
        ItineraryRequest itineraryRequest = new ItineraryRequest(-1L, null);
        RequestProcessor.run(itineraryRequest);
    }

    @Test(expected = EanWsError.class)
    public void testItineraryLookupInvalidItineraryId() throws Exception {
        ItineraryRequest itineraryRequest = new ItineraryRequest(1234L, EMAIL);
        RequestProcessor.run(itineraryRequest);
    }

    @Test(expected = EanWsError.class)
    public void testItineraryLookupInvalidEmail() throws Exception {
        Reservation testReservation = BookingRequestIntTest.getTestReservation();
        ItineraryRequest itineraryRequest
            = new ItineraryRequest(testReservation.itineraryId, "invalid@expedia.com");
        RequestProcessor.run(itineraryRequest);
    }

    @Test
    public void testItineraryLookupValidUs() throws Exception {
        doItineraryLookupValid();
    }

    @Test
    public void testItineraryLookupValidItaly() throws Exception {
        CommonParameters.locale = Locale.ITALY.toString();
        CommonParameters.currencyCode = Currency.getInstance(Locale.ITALY).getCurrencyCode();
        doItineraryLookupValid();
    }

    private void doItineraryLookupValid() throws Exception {
        Reservation testReservation = BookingRequestIntTest.getTestReservation();
        ItineraryRequest itineraryRequest
            = new ItineraryRequest(testReservation.itineraryId, EMAIL);
        Itinerary itinerary = RequestProcessor.run(itineraryRequest);
        assertNotNull(itinerary);
        assertNotNull(itinerary.hotelConfirmations);
        assertThat(itinerary.hotelConfirmations.size(), greaterThan(0));

        Itinerary.HotelConfirmation hotelConfirmation = itinerary.hotelConfirmations.get(0);
        assertNotNull(hotelConfirmation);
        assertEquals(ConfirmationStatus.CONFIRMED, hotelConfirmation.status);
        assertEquals(CommonParameters.locale, hotelConfirmation.locale);
        assertEquals(CommonParameters.currencyCode, hotelConfirmation.rate.chargeable.currencyCode);
    }
}
