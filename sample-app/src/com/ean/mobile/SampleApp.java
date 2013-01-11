package com.ean.mobile;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.*;

/**
 * Copyright (c) 2002-2012 EAN.com, L.P. All rights reserved.
 */
public class SampleApp extends Application {

    public static final Locale LOCALE = Locale.US;

    public static final Currency CURRENCY = Currency.getInstance(LOCALE);

    public static String searchQuery;
    public static int numberOfAdults;
    public static int numberOfChildren;
    public static LocalDate arrivalDate;
    public static LocalDate departureDate;

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

    public static void sendRedirectionToast(final Context context) {
        Toast.makeText(context, R.string.redirected, Toast.LENGTH_LONG).show();
    }
}
