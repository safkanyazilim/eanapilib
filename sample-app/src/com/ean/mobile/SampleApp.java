package com.ean.mobile;

import android.app.Application;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2002-2012 EAN.com, L.P. All rights reserved.
 */
public class SampleApp extends Application {

    public static String searchQuery;
    public static int numberOfAdults;
    public static int numberOfChildren;
    public static DateTime arrivalDate;
    public static DateTime departureDate;

    // When a new search is performed, foundHotels, selectedHotel,
    // EXTENDED_INFOS, and HOTEL_ROOMS should be cleared or nullified, as appropriate.
    public static HotelInfoList foundHotels;

    public static HotelInfo selectedHotel;

    public static HotelRoom selectedRoom;

    public static final Map<Long, HotelInfoExtended> EXTENDED_INFOS
            = Collections.synchronizedMap(new HashMap<Long, HotelInfoExtended>());

    public static final Map<Long, List<HotelRoom>> HOTEL_ROOMS
            = Collections.synchronizedMap(new HashMap<Long, List<HotelRoom>>());

    public static RoomOccupancy occupancy() {
        return new RoomOccupancy(SampleApp.numberOfAdults, SampleApp.numberOfChildren);
    }
}
