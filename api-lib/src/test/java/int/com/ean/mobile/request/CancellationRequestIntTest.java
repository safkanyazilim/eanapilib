/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.junit.Ignore;
import org.junit.Test;

import com.ean.mobile.Address;
import com.ean.mobile.BasicAddress;
import com.ean.mobile.Cancellation;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.Reservation;
import com.ean.mobile.ReservationRoom;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.EanWsError;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CancellationRequestIntTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);
    private static final Address ADDRESS = new BasicAddress("travelnow", "Seattle", "WA", "US", "98004");
    private static final String EMAIL = "test@expedia.com";

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidItineraryIdEmailConfirmationNumber() throws Exception {
        CancellationRequest cancellationRequest = new CancellationRequest(-1L, -1L, null, null);
        RequestProcessor.run(cancellationRequest);
    }

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidItineraryId() throws Exception {
        Reservation testReservation = getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            -1L, testReservation.confirmationNumbers.get(0), EMAIL, null);
        RequestProcessor.run(cancellationRequest);
    }

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidConfirmationNumber() throws Exception {
        Reservation testReservation = getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            testReservation.itineraryId, -1L, EMAIL, null);
        RequestProcessor.run(cancellationRequest);
    }

    @Test(expected = EanWsError.class)
    public void testCancellationInvalidEmail() throws Exception {
        Reservation testReservation = getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            testReservation.itineraryId, testReservation.confirmationNumbers.get(0), "invalid@expedia.com", null);
        RequestProcessor.run(cancellationRequest);
    }

    @Ignore("Will never pass because test cancellations always return an EanWsError. It is here only for reference.")
    @Test
    public void testCancellationValid() throws Exception {
        Reservation testReservation = getTestReservation();
        CancellationRequest cancellationRequest = new CancellationRequest(
            testReservation.itineraryId, testReservation.confirmationNumbers.get(0), EMAIL, null);

        Cancellation cancellation = RequestProcessor.run(cancellationRequest);
        assertNotNull(cancellation);
        assertThat(cancellation.cancellationNumber, not(isEmptyOrNullString()));
    }

    private Reservation getTestReservation() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(10, 13);
        ListRequest hotelListRequest = new ListRequest("Seattle, WA", OCCUPANCY,
            dateTimes[0], dateTimes[1], null, Locale.US.toString(), "en_US");
        HotelInfoList hotelList = RequestProcessor.run(hotelListRequest);

        RoomAvailRequest roomAvailRequest = new RoomAvailRequest(hotelList.hotelInfos.get(0).hotelId, OCCUPANCY,
            dateTimes[0], dateTimes[1], hotelList.customerSessionId, Locale.US.toString(), "en_US");

        List<HotelRoom> rooms = RequestProcessor.run(roomAvailRequest);
        BookingRequest.ReservationInfo resInfo = new BookingRequest.ReservationInfo(
            EMAIL, "test", "tester", "1234567890", null, "CA", "5401999999999999", "123", YearMonth.now().plusYears(1));

        ReservationRoom room
            = new ReservationRoom(resInfo.individual.name, rooms.get(0), rooms.get(0).bedTypes.get(0).id, OCCUPANCY);

        BookingRequest bookingRequest = new BookingRequest(
            hotelList.hotelInfos.get(0).hotelId, dateTimes[0], dateTimes[1],
            hotelList.hotelInfos.get(0).supplierType, room, resInfo, ADDRESS,
            hotelList.customerSessionId, Locale.US.toString(), "en_US");

        return RequestProcessor.run(bookingRequest);
    }

}
