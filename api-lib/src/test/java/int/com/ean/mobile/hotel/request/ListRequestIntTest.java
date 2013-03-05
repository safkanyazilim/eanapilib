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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.ean.mobile.exception.DataValidationException;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelList;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.DateModifier;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.request.RequestTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ListRequestIntTest extends RequestTestBase {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    @Test
    public void testSearchForHotelsHappy() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest listRequest = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        HotelList results = RequestProcessor.run(listRequest);

        assertEquals(10, results.hotels.size());
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsCauseError() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, -3);

        ListRequest listRequest
            = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);

        RequestProcessor.run(listRequest);
    }

    @Test(expected = DataValidationException.class)
    public void testSearchForHotelsLocationException() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest listRequest = new ListRequest(
            "sea of tranquility, moon", OCCUPANCY, dateTimes[0], dateTimes[1]);

        RequestProcessor.run(listRequest);
    }

    @Test
    public void testSearchForHotelsMultiRoomType() throws Exception {
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        List<RoomOccupancy> occupancies = Arrays.asList(OCCUPANCY, new RoomOccupancy(1, Arrays.asList(4, 5, 7)));

        ListRequest listRequest = new ListRequest("rome, it", occupancies, dateTimes[0], dateTimes[1]);

        HotelList results = RequestProcessor.run(listRequest);

        assertEquals(10, results.hotels.size());
    }

    @Test
    public void testSearchForHotelsPaging() throws Exception {
        Set<Long> hotelIdsReturned = new HashSet<Long>();
        LocalDate[] dateTimes = DateModifier.getAnArrayOfLocalDatesWithOffsets(1, 3);

        ListRequest listRequest = new ListRequest("rome, it", OCCUPANCY, dateTimes[0], dateTimes[1]);
        HotelList results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        // Paginate a few times and make sure they are ordered correctly.
        listRequest = new ListRequest(results.cacheKey, results.cacheLocation);
        results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        listRequest = new ListRequest(results.cacheKey, results.cacheLocation);
        results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);

        listRequest = new ListRequest(results.cacheKey, results.cacheLocation);
        results = RequestProcessor.run(listRequest);
        checkForDuplicateHotelId(hotelIdsReturned, results);
    }

    private void checkForDuplicateHotelId(final Set<Long> hotelIdsReturned, final HotelList results) {
        for (Hotel hotel : results.hotels) {
            assertFalse(hotelIdsReturned.contains(hotel.hotelId));
            hotelIdsReturned.add(hotel.hotelId);
        }
    }

}
