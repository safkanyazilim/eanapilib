package com.ean.mobile.request;

import com.ean.mobile.Address;
import com.ean.mobile.BasicAddress;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.Name;
import com.ean.mobile.RoomOccupancy;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class BookingRequestIntTest {

    private static final RoomOccupancy OCCUPANCY = new RoomOccupancy(1, null);

    private static final Address ADDRESS = new BasicAddress("travelnow", "Seattle", "WA", "US", "98004");

    @Test
    public void testBookSingularRoomInSeattle() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(10, 13);
        HotelInfoList hotelList = ListRequest.searchForHotels("Seattle, WA", OCCUPANCY, dateTimes[0], dateTimes[1]);

        List<HotelRoom> rooms = RoomAvailRequest.getRoomAvail(
                hotelList.get(0).hotelId,
                OCCUPANCY,
                dateTimes[0],
                dateTimes[1],
                hotelList.customerSessionId);

        BookingRequest.ReservationInfo resInfo = new BookingRequest.ReservationInfo(
                "test@expedia.com",
                "test",
                "tester",
                "1234567890",
                null,
                "CA",
                "5401999999999999",
                "123",
                DateTime.now().plusYears(1)
        );

        BookingRequest.Room room = new BookingRequest.Room(
                resInfo.individual.name,
                rooms.get(0),
                rooms.get(0).bedTypes.get(0).id,
                OCCUPANCY);


        BookingRequest.performBooking(
                hotelList.get(0).hotelId,
                dateTimes[0],
                dateTimes[1],
                hotelList.get(0).supplierType,
                room,
                resInfo,
                ADDRESS,
                hotelList.customerSessionId
        );

        assertTrue(true);
    }

    @Test
    public void testBookMultiRoomInSeattle() throws Exception {
        DateTime[] dateTimes = DateModifier.getAnArrayOfDateTimesWithOffsets(10, 13);

        List<RoomOccupancy> occupancies = Arrays.asList(
                OCCUPANCY,
                new RoomOccupancy(1, 3)
        );
        HotelInfoList hotelList = ListRequest.searchForHotels("Seattle, WA", occupancies, dateTimes[0], dateTimes[1]);

        List<HotelRoom> rooms = RoomAvailRequest.getRoomAvail(
                hotelList.get(0).hotelId,
                occupancies,
                dateTimes[0],
                dateTimes[1],
                hotelList.customerSessionId);

        List<Name> checkInNames = Arrays.asList(
                new Name("test", "tester"),
                new Name("test", "testerson")
        );

        BookingRequest.ReservationInfo resInfo = new BookingRequest.ReservationInfo(
                "test@expedia.com",
                checkInNames.get(0).first,
                checkInNames.get(0).last,
                "1234567890",
                null,
                "CA",
                "5401999999999999",
                "123",
                DateTime.now().plusYears(1)
        );




        List<BookingRequest.Room> bookingRooms = Arrays.asList(
                new BookingRequest.Room(
                    checkInNames.get(0),
                    rooms.get(0),
                    rooms.get(0).bedTypes.get(0).id,
                    occupancies.get(0)),
                new BookingRequest.Room(
                        checkInNames.get(1),
                        rooms.get(0),
                        rooms.get(0).bedTypes.get(0).id,
                        occupancies.get(1))
        );


        BookingRequest.performBooking(
                hotelList.get(0).hotelId,
                dateTimes[0],
                dateTimes[1],
                hotelList.get(0).supplierType,
                bookingRooms,
                resInfo,
                ADDRESS,
                hotelList.customerSessionId
        );

        assertTrue(true);
    }
}
