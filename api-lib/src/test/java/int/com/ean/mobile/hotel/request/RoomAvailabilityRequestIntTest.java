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
