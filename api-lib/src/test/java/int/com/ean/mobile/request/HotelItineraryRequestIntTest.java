/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.junit.Test;

import com.ean.mobile.Address;
import com.ean.mobile.BasicAddress;
import com.ean.mobile.ConfirmationStatus;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.Itinerary;
import com.ean.mobile.Reservation;
import com.ean.mobile.ReservationRoom;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.EanWsError;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class HotelItineraryRequestIntTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);
    private static final Address ADDRESS = new BasicAddress("travelnow", "Seattle", "WA", "US", "98004");
    private static final String EMAIL = "test@expedia.com";

    @Test(expected = EanWsError.class)
    public void testItineraryLookupInvalidItineraryIdAndEmail() throws Exception {
        HotelItineraryRequest hotelItineraryRequest = new HotelItineraryRequest(-1L, null);
        RequestProcessor.run(hotelItineraryRequest);
    }

    @Test(expected = EanWsError.class)
    public void testItineraryLookupInvalidItineraryId() throws Exception {
        HotelItineraryRequest hotelItineraryRequest = new HotelItineraryRequest(1234L, EMAIL);
        RequestProcessor.run(hotelItineraryRequest);
    }

    @Test(expected = EanWsError.class)
    public void testItineraryLookupInvalidEmail() throws Exception {
        Reservation testReservation = getTestReservation(Locale.US.toString(), "USD");
        HotelItineraryRequest hotelItineraryRequest
            = new HotelItineraryRequest(testReservation.itineraryId, "invalid@expedia.com");
        RequestProcessor.run(hotelItineraryRequest);
    }

    @Test
    public void testItineraryLookupValidUs() throws Exception {
        doItineraryLookupValid(Locale.US.toString(), "USD");
    }

    @Test
    public void testItineraryLookupValidItaly() throws Exception {
        doItineraryLookupValid(Locale.ITALY.toString(), "EUR");
    }

    private void doItineraryLookupValid(final String locale, final String currencyCode) throws Exception {
        Reservation testReservation = getTestReservation(locale, currencyCode);
        HotelItineraryRequest hotelItineraryRequest
            = new HotelItineraryRequest(testReservation.itineraryId, EMAIL, locale, currencyCode);
        Itinerary itinerary = RequestProcessor.run(hotelItineraryRequest);

        assertNotNull(itinerary);
        assertNotNull(itinerary.hotelConfirmations);
        assertThat(itinerary.hotelConfirmations.size(), greaterThan(0));
        Itinerary.HotelConfirmation hotelConfirmation = itinerary.hotelConfirmations.get(0);
        assertNotNull(hotelConfirmation);
        assertEquals(ConfirmationStatus.CONFIRMED, hotelConfirmation.status);
        assertEquals(locale, hotelConfirmation.locale);
        assertEquals(currencyCode, hotelConfirmation.rate.chargeable.currencyCode);
    }

    private Reservation getTestReservation(final String locale, final String currencyCode) throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(10, 13);
        ListRequest hotelListRequest = new ListRequest("Seattle, WA", OCCUPANCY,
            dateTimes[0], dateTimes[1], null, locale, currencyCode);
        HotelInfoList hotelList = RequestProcessor.run(hotelListRequest);

        RoomAvailRequest roomAvailRequest = new RoomAvailRequest(hotelList.hotelInfos.get(0).hotelId, OCCUPANCY,
            dateTimes[0], dateTimes[1], hotelList.customerSessionId, locale, currencyCode);

        List<HotelRoom> rooms = RequestProcessor.run(roomAvailRequest);
        BookingRequest.ReservationInfo resInfo = new BookingRequest.ReservationInfo(
            EMAIL, "test", "tester", "1234567890", null, "CA", "5401999999999999", "123", YearMonth.now().plusYears(1));

        ReservationRoom room
            = new ReservationRoom(resInfo.individual.name, rooms.get(0), rooms.get(0).bedTypes.get(0).id, OCCUPANCY);

        BookingRequest bookingRequest = new BookingRequest(
            hotelList.hotelInfos.get(0).hotelId, dateTimes[0], dateTimes[1],
            hotelList.hotelInfos.get(0).supplierType, room, resInfo, ADDRESS,
            hotelList.customerSessionId, locale, currencyCode);

        return RequestProcessor.run(bookingRequest);
    }
}
