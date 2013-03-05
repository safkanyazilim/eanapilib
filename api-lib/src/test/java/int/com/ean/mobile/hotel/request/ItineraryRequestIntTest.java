/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
 */

package com.ean.mobile.hotel.request;

import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;

import com.ean.mobile.Address;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.ConfirmationStatus;
import com.ean.mobile.hotel.Itinerary;
import com.ean.mobile.hotel.Reservation;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.request.RequestTestBase;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ItineraryRequestIntTest extends RequestTestBase {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);
    private static final Address ADDRESS = new Address(Arrays.asList("travelnow"), "Seattle", "WA", "US", "98004");
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
