package com.ean.mobile;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright (c) 2002-2012 EAN.com, L.P. All rights reserved.
 */
public class SampleApp extends Application {

    public static final Locale DEFAULT_LOCALE = Locale.US;
    public static Locale locale = DEFAULT_LOCALE;

    public static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_LOCALE);
    public static Currency currency = DEFAULT_CURRENCY;

    public static String searchQuery;
    public static int numberOfAdults;
    public static int numberOfChildren;
    public static LocalDate arrivalDate;
    public static LocalDate departureDate;

    // When a new search is performed, foundHotels, selectedHotel,
    // EXTENDED_INFOS, and HOTEL_ROOMS should be cleared or nullified, as appropriate.
    public static List<HotelInfo> foundHotels;
    public static String cacheKey;
    public static String cacheLocation;

    public static String customerSessionId;

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

    public static void updateFoundHotels(HotelInfoList hotelInfoList) {
        updateFoundHotels(hotelInfoList, false);
    }

    public static synchronized void updateFoundHotels(HotelInfoList hotelInfoList, boolean clearOnUpdate) {
        if (SampleApp.foundHotels == null) {
            SampleApp.foundHotels = new ArrayList<HotelInfo>();
        } else if (clearOnUpdate) {
            SampleApp.foundHotels.clear();
        }
        SampleApp.foundHotels.addAll(hotelInfoList.hotelInfos);
        SampleApp.customerSessionId = hotelInfoList.customerSessionId;
        SampleApp.cacheKey = hotelInfoList.cacheKey;
        SampleApp.cacheLocation = hotelInfoList.cacheLocation;
    }
}
