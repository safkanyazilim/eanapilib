/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.LocalDate;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;


import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelImageTuple;
import com.ean.mobile.hotel.HotelInformation;
import com.ean.mobile.hotel.HotelList;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.Reservation;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.CommonParameters;

/**
 * Copyright (c) 2002-2012 EAN.com, L.P. All rights reserved.
 */
public final class SampleApp extends Application {

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

    public static Hotel selectedHotel;

    public static HotelRoom selectedRoom;

    public static final Map<Long, HotelInformation> EXTENDED_INFOS
            = Collections.synchronizedMap(new HashMap<Long, HotelInformation>());

    public static final Map<Long, List<HotelRoom>> HOTEL_ROOMS
            = Collections.synchronizedMap(new HashMap<Long, List<HotelRoom>>());

    public static final Map<HotelImageTuple, HotelImageDrawable> IMAGE_DRAWABLES
            = Collections.synchronizedMap(new HotelImageDrawableMap());

    private static final Set<Reservation> RESERVATIONS = new TreeSet<Reservation>();

    @Override
    public void onCreate() {
        super.onCreate();
        CommonParameters.cid = "55505";
        CommonParameters.apiKey = "";
        CommonParameters.customerUserAgent = "Android";
        CommonParameters.locale = Locale.US.toString();
        CommonParameters.currencyCode = Currency.getInstance(Locale.US).getCurrencyCode();
    }
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
        if (hotelList != null) {
            SampleApp.foundHotels.addAll(hotelList.hotels);
            SampleApp.cacheKey = hotelList.cacheKey;
            SampleApp.cacheLocation = hotelList.cacheLocation;
        }
    }

    public static void addReservationToCache(final Context context, final Reservation reservation) {
        RESERVATIONS.add(reservation);
    }

    public static Reservation getLatestReservation() {
        return RESERVATIONS.size() == 0 ? null : RESERVATIONS.iterator().next();
    }
}
