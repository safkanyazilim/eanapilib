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

import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelImageTuple;
import com.ean.mobile.hotel.HotelInformation;
import com.ean.mobile.hotel.HotelList;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.RoomOccupancy;

/**
 * Copyright (c) 2002-2012 EAN.com, L.P. All rights reserved.
 */
public class SampleApp extends Application {

    private static final Locale DEFAULT_LOCALE = Locale.US;
    public static Locale locale = DEFAULT_LOCALE;

    private static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_LOCALE);
    public static Currency currency = DEFAULT_CURRENCY;

    public static String searchQuery;
    public static int numberOfAdults;
    public static int numberOfChildren;
    public static LocalDate arrivalDate;
    public static LocalDate departureDate;

    // When a new search is performed, foundHotels, selectedHotel,
    // EXTENDED_INFOS, and HOTEL_ROOMS should be cleared or nullified, as appropriate.
    public static List<Hotel> foundHotels;
    public static String cacheKey;
    public static String cacheLocation;

    public static String customerSessionId;

    public static Hotel selectedHotel;

    public static HotelRoom selectedRoom;

    public static final Map<Long, HotelInformation> EXTENDED_INFOS
            = Collections.synchronizedMap(new HashMap<Long, HotelInformation>());

    public static final Map<Long, List<HotelRoom>> HOTEL_ROOMS
            = Collections.synchronizedMap(new HashMap<Long, List<HotelRoom>>());

    public static final Map<HotelImageTuple, HotelImageDrawable> IMAGE_DRAWABLES
            = Collections.synchronizedMap(new HotelImageDrawableMap());

    public static RoomOccupancy occupancy() {
        return new RoomOccupancy(SampleApp.numberOfAdults, SampleApp.numberOfChildren);
    }

    public static void sendRedirectionToast(final Context context) {
        Toast.makeText(context, R.string.redirected, Toast.LENGTH_LONG).show();
    }

    public static void resetDates() {
        LocalDate today = LocalDate.now();
        arrivalDate = today;
        departureDate = today.plusDays(1);
    }

    public static void clearSearch() {
        searchQuery = null;
        numberOfAdults = 0;
        numberOfChildren = 0;
        arrivalDate = null;
        departureDate = null;
        foundHotels = null;
        selectedHotel = null;
        selectedRoom = null;
        EXTENDED_INFOS.clear();
        HOTEL_ROOMS.clear();
        IMAGE_DRAWABLES.clear();
    }

    public static void updateFoundHotels(HotelList hotelList) {
        updateFoundHotels(hotelList, false);
    }

    public static synchronized void updateFoundHotels(HotelList hotelList, boolean clearOnUpdate) {
        if (SampleApp.foundHotels == null) {
            SampleApp.foundHotels = new ArrayList<Hotel>();
        } else if (clearOnUpdate) {
            SampleApp.foundHotels.clear();
        }
        SampleApp.foundHotels.addAll(hotelList.hotels);
        SampleApp.customerSessionId = hotelList.customerSessionId;
        SampleApp.cacheKey = hotelList.cacheKey;
        SampleApp.cacheLocation = hotelList.cacheLocation;
    }
}
