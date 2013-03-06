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
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
 */

package com.ean.mobile.hotel.request;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.ean.mobile.Address;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.Cancellation;
import com.ean.mobile.hotel.Reservation;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.request.RequestTestBase;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CancellationRequestIntTest extends RequestTestBase {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);
    private static final Address ADDRESS = new Address(Arrays.asList("travelnow"), "Seattle", "WA", "US", "98004");
    private static final String EMAIL = "test@expedia.com";

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidItineraryIdEmailConfirmationNumber() throws Exception {
        CancellationRequest cancellationRequest = new CancellationRequest(-1L, -1L, null, null);
        RequestProcessor.run(cancellationRequest);
    }

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidItineraryId() throws Exception {
        Reservation testReservation = BookingRequestIntTest.getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            -1L, testReservation.confirmationNumbers.get(0), EMAIL, null);
        RequestProcessor.run(cancellationRequest);
    }

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidConfirmationNumber() throws Exception {
        Reservation testReservation = BookingRequestIntTest.getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            testReservation.itineraryId, -1L, EMAIL, null);
        RequestProcessor.run(cancellationRequest);
    }

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidEmail() throws Exception {
        Reservation testReservation = BookingRequestIntTest.getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            testReservation.itineraryId, testReservation.confirmationNumbers.get(0), "invalid@expedia.com", null);
        RequestProcessor.run(cancellationRequest);
    }

    @Ignore("Will never pass because test cancellations always return an EanWsError. It is here only for reference.")
    @Test
    public void testCancellationValid() throws Exception {
        Reservation testReservation = BookingRequestIntTest.getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            testReservation.itineraryId, testReservation.confirmationNumbers.get(0), EMAIL, null);

        Cancellation cancellation = RequestProcessor.run(cancellationRequest);
        assertNotNull(cancellation);
        assertThat(cancellation.cancellationNumber, not(isEmptyOrNullString()));
    }

}
