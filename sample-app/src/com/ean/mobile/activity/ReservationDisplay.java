/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.activity;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ean.mobile.R;
import com.ean.mobile.SampleApp;
import com.ean.mobile.hotel.Reservation;

public class ReservationDisplay extends Activity {

    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.reservationdisplay);

        final Reservation reservationToDisplay = SampleApp.getLatestReservation();


        final TableLayout infoList = (TableLayout) findViewById(R.id.reservationInfoList);

        final ViewAdder adder = new ViewAdder(infoList, getLayoutInflater());

        adder.addKeyValue("Itinerary ID", reservationToDisplay.itineraryId);
        adder.addKeyValue("Confirmation Numbers", TextUtils.join(",", reservationToDisplay.confirmationNumbers));
        adder.addKeyValue("Check-In Instructions", reservationToDisplay.checkInInstructions);
        adder.addKeyValue("Arrival Date", DATE_FORMATTER.print(reservationToDisplay.arrivalDate));
        adder.addKeyValue("Departure Date", DATE_FORMATTER.print(reservationToDisplay.departureDate));
        adder.addKeyValue("Hotel Name", reservationToDisplay.hotelName);
        adder.addKeyValue("Hotel Address", reservationToDisplay.hotelAddress.toString());
        adder.addKeyValue("Room Description", reservationToDisplay.roomDescription);

    }

    private static class ViewAdder {

        private final TableLayout table;

        private final LayoutInflater inflater;

        public ViewAdder(final TableLayout table, final LayoutInflater inflater) {
            this.table = table;
            this.inflater = inflater;
        }
        
        public void addKeyValue(final String key, final Object value) {
            table.addView(inflateKeyValue(key, value));
        }

        private View inflateKeyValue(final String key, final Object value) {
            final View view = inflater.inflate(R.layout.reservationinfolistlayout, null);

            final TextView keyView = (TextView) view.findViewById(R.id.reservationinfokey);
            keyView.setText(key);

            final TextView valueView = (TextView) view.findViewById(R.id.reservationinfovalue);
            valueView.setText(value.toString());
            return view;
        }
    }
}
