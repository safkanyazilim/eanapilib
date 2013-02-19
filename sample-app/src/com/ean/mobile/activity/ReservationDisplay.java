/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.activity;

import android.content.Context;
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

/**
 * The code behind the ReservationDisplay activity.
 */
public class ReservationDisplay extends Activity {

    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.reservationdisplay);

        final Reservation reservationToDisplay = SampleApp.getLatestReservation();


        final TableLayout infoList = (TableLayout) findViewById(R.id.reservationInfoList);

        final ViewAdder adder = new ViewAdder(infoList, getLayoutInflater(), getApplicationContext());

        adder.addKeyValue(R.string.itinerary_id, reservationToDisplay.itineraryId);
        adder.addKeyValue(R.string.confirmation_numbers, TextUtils.join(",", reservationToDisplay.confirmationNumbers));
        adder.addKeyValue(R.string.checkin_instructions, reservationToDisplay.checkInInstructions);
        adder.addKeyValue(R.string.arrival_date, DATE_FORMATTER.print(reservationToDisplay.arrivalDate));
        adder.addKeyValue(R.string.departure_date, DATE_FORMATTER.print(reservationToDisplay.departureDate));
        adder.addKeyValue(R.string.hotel_name, reservationToDisplay.hotelName);
        adder.addKeyValue(R.string.hotel_address, reservationToDisplay.hotelAddress.toString());
        adder.addKeyValue(R.string.room_description, reservationToDisplay.roomDescription);

    }

    private static class ViewAdder {

        private final TableLayout table;
        private final LayoutInflater inflater;
        private final Context context;

        public ViewAdder(final TableLayout table, final LayoutInflater inflater, final Context context) {
            this.table = table;
            this.inflater = inflater;
            this.context = context;
        }
        
        public void addKeyValue(final int keyResId, final Object value) {
            String key = context.getString(keyResId);
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
