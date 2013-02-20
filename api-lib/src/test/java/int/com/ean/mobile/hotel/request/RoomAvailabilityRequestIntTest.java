/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel.request;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.ean.mobile.exception.DataValidationException;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.DateModifier;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.request.RequestTestBase;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class RoomAvailabilityRequestIntTest extends RequestTestBase {

    private static final long HOTEL_IN_SEATTLE = 106347L;

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Test
    public void testGetGoodAvailability() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);
        try {
            RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, OCCUPANCY,
                dateTimes[0], dateTimes[1]);
            List<HotelRoom> rooms = RequestProcessor.run(roomAvailabilityRequest);
            assertThat(rooms.size(), greaterThan(0));
        } catch (EanWsError ewe) {
            assertEquals("SOLD_OUT", ewe.category);
        }
    }

    @Test
    public void testMultiRoomAvailability() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);
        try {
            RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, OCCUPANCY,
                dateTimes[0], dateTimes[1]);
            List<HotelRoom> rooms = RequestProcessor.run(roomAvailabilityRequest);
            assertThat(rooms.size(), greaterThan(0));
        } catch (EanWsError ewe) {
            assertEquals("SOLD_OUT", ewe.category);
        }
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailabilityWrongDates() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, -3);
        RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest(
            HOTEL_IN_SEATTLE, OCCUPANCY, dateTimes[0], dateTimes[1]);
        RequestProcessor.run(roomAvailabilityRequest);
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailabilityDateInThePast() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(-1, 3);
        RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest(
            HOTEL_IN_SEATTLE, OCCUPANCY, dateTimes[0], dateTimes[1]);
        RequestProcessor.run(roomAvailabilityRequest);
    }

    @Test(expected = DataValidationException.class)
    public void testGetAvailabilityBadHotel() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);
        RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest(
            -1L, new RoomOccupancy(1, null), dateTimes[0], dateTimes[1]);
        RequestProcessor.run(roomAvailabilityRequest);
    }


    @Test
    public void testGetGoodAvailabilityMultiRoom() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);
        List<RoomOccupancy> occupancies = Arrays.asList(OCCUPANCY, new RoomOccupancy(1, Arrays.asList(4, 5, 7)));
        try {
            RoomAvailabilityRequest roomAvailabilityRequest = new RoomAvailabilityRequest(HOTEL_IN_SEATTLE, occupancies,
                dateTimes[0], dateTimes[1]);
            List<HotelRoom> rooms = RequestProcessor.run(roomAvailabilityRequest);
            assertNotNull(rooms);
            assertThat(rooms.size(), greaterThan(0));
            assertEquals(2, rooms.get(0).rate.roomGroup.size());
        } catch (EanWsError ewe) {
            assertEquals("SOLD_OUT", ewe.category);
        }
    }
}
