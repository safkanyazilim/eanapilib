/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel.request;

import org.junit.Ignore;
import org.junit.Test;

import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.Cancellation;
import com.ean.mobile.hotel.Reservation;
import com.ean.mobile.request.BaseRequestTest;
import com.ean.mobile.request.RequestProcessor;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CancellationRequestIntTest extends BaseRequestTest {

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
