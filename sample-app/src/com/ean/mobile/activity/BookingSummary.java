/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.activity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

import android.widget.*;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ean.mobile.Address;
import com.ean.mobile.R;
import com.ean.mobile.app.SampleApp;
import com.ean.mobile.app.SampleConstants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.NightlyRate;
import com.ean.mobile.hotel.Reservation;
import com.ean.mobile.hotel.ReservationRoom;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.hotel.request.BookingRequest;
import com.ean.mobile.request.RequestProcessor;

/**
 * The code behind the BookingSummary layout. Manages everything that goes on screen in that page, as well
 * as handles the button clicks and starts the reservation process.
 */
public class BookingSummary extends Activity {

    private static final String DATE_FORMAT_STRING = "EEEE, MMMM dd, yyyy";
    private static final String NIGHTLY_RATE_FORMAT_STRING = "MM-dd-yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);
    private static final DateTimeFormatter NIGHTLY_RATE_FORMATTER
        = DateTimeFormat.forPattern(NIGHTLY_RATE_FORMAT_STRING);
    private static final int PICK_CONTACT_INTENT = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.bookingsummary);

        final TextView hotelName = (TextView) findViewById(R.id.hotelName);
        final TextView checkIn = (TextView) findViewById(R.id.arrivalDisplay);
        final TextView checkOut = (TextView) findViewById(R.id.departureDisplay);
        final TextView numGuests = (TextView) findViewById(R.id.guestsNumberDisplay);
        final TextView roomType = (TextView) findViewById(R.id.roomTypeDisplay);
        final TextView bedType = (TextView) findViewById(R.id.bedTypeDisplay);
        final TextView taxesAndFees = (TextView) findViewById(R.id.taxes_and_fees_display);
        final TextView totalLowPrice = (TextView) findViewById(R.id.lowPrice);
        final Hotel hotel = SampleApp.selectedHotel;
        final HotelRoom hotelRoom = SampleApp.selectedRoom;
        final RoomOccupancy occupancy = hotelRoom.rate.roomGroup.get(0).occupancy;

        hotelName.setText(hotel.name);
        checkIn.setText(DATE_TIME_FORMATTER.print(SampleApp.arrivalDate));
        checkOut.setText(DATE_TIME_FORMATTER.print(SampleApp.departureDate));
        numGuests.setText(String.format(
            getString(R.string.adults_comma_children), occupancy.numberOfAdults, occupancy.childAges.size()));
        roomType.setText(hotelRoom.description);
        bedType.setText(hotelRoom.bedTypes.get(0).description);

        final NumberFormat currencyFormat = getCurrencyFormat(hotel.currencyCode);

        taxesAndFees.setText(currencyFormat.format(hotelRoom.getTaxesAndFees()));

        totalLowPrice.setText(currencyFormat.format(hotelRoom.getTotalRate()));

        displayTotalHighPrice(hotelRoom, hotel.highPrice, currencyFormat);
        populatePriceBreakdownList(currencyFormat);
    }

    private void displayTotalHighPrice(final HotelRoom hotelRoom, final BigDecimal highPrice,
             final NumberFormat currencyFormat) {
        final TextView totalHighPrice = (TextView) findViewById(R.id.highPrice);
        final ImageView drrIcon = (ImageView) findViewById(R.id.drrPromoImg);
        final TextView drrPromoText = (TextView) findViewById(R.id.drrPromoText);

        if (hotelRoom.getTotalRate().equals(hotelRoom.getTotalBaseRate())) {
            // if there's no promo, then we make the promo stuff disappear.
            totalHighPrice.setVisibility(TextView.GONE);
            drrIcon.setVisibility(ImageView.GONE);
            drrPromoText.setVisibility(ImageView.GONE);
        } else {
            // if there is a promo, we make it show up.
            drrPromoText.setText(hotelRoom.promoDescription);
            totalHighPrice.setVisibility(TextView.VISIBLE);
            drrIcon.setVisibility(ImageView.VISIBLE);
            drrPromoText.setVisibility(ImageView.VISIBLE);
            totalHighPrice.setText(currencyFormat.format(highPrice));
            totalHighPrice.setPaintFlags(totalHighPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private NumberFormat getCurrencyFormat(final String currencyCode) {
        final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(Currency.getInstance(currencyCode));
        return currencyFormat;
    }

    private void populatePriceBreakdownList(final NumberFormat currencyFormat) {
        final LinearLayout priceBreakdownList = (LinearLayout) findViewById(R.id.priceDetailsBreakdown);
        View view;
        final LayoutInflater inflater = getLayoutInflater();

        LocalDate currentDate = SampleApp.arrivalDate.minusDays(1);
        for (NightlyRate rate : SampleApp.selectedRoom.rate.chargeable.nightlyRates) {
            view = inflater.inflate(R.layout.pricebreakdownlayout, null);
            final TextView date = (TextView) view.findViewById(R.id.priceBreakdownDate);
            final TextView highPrice = (TextView) view.findViewById(R.id.priceBreakdownHighPrice);
            final TextView lowPrice = (TextView) view.findViewById(R.id.priceBreakdownLowPrice);

            currentDate = currentDate.plusDays(1);
            date.setText(NIGHTLY_RATE_FORMATTER.print(currentDate));

            lowPrice.setText(currencyFormat.format(rate.rate));
            if (rate.rate.equals(rate.baseRate)) {
                highPrice.setVisibility(TextView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                highPrice.setText(currencyFormat.format(rate.baseRate));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            priceBreakdownList.addView(view);
        }
    }

    /**
     * (Event handler) Contains the action to handle the contact choose button.
     * @param view The view that fired this event.
     */
    public void onContactChooseButtonClick(final View view) {
        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_INTENT);
    }

    /**
     * (Event handler) Contains the action to handle the load default billing info button.
     * @param view The view that fired this event.
     */
    public void onLoadDefaultBillingInfoClick(final View view) {
        final EditText addressLine1 = (EditText) findViewById(R.id.billingInformationAddress1);
        final EditText addressLine2 = (EditText) findViewById(R.id.billingInformationAddress2);
        final EditText city = (EditText) findViewById(R.id.billingInformationCity);
        final EditText state = (EditText) findViewById(R.id.billingInformationState);
        final EditText country = (EditText) findViewById(R.id.billingInformationCountry);
        final EditText zip = (EditText) findViewById(R.id.billingInformationZip);

        final Spinner cardType = (Spinner) findViewById(R.id.billingInformationCCType);
        final EditText cardNum = (EditText) findViewById(R.id.billingInformationCCNum);
        final EditText cardExpirationMonth = (EditText) findViewById(R.id.billingInformationCCExpMo);
        final EditText cardExpirationYear = (EditText) findViewById(R.id.billingInformationCCExpYr);
        final EditText cardSecurityCode = (EditText) findViewById(R.id.billingInformationCCSecurityCode);

        final int yearsInACentury = 100;

        //sorry, but it's just so simple
        addressLine1.setText("travelnow");
        addressLine2.setText("");
        city.setText("Seattle");
        state.setText("WA");
        country.setText("US");
        zip.setText("98004");
        cardType.setSelection(
            Arrays.asList(getResources().getStringArray(R.array.supported_credit_cards)).indexOf("CA"));
        cardNum.setText("5401999999999999");
        cardExpirationMonth.setText("01");
        cardExpirationYear.setText(Integer.toString((YearMonth.now().getYear() + 1) % yearsInACentury));
        cardSecurityCode.setText("123");

    }

    /**
     * (Event hanlder) Handles the complete booking button click. Loads the information from the inputs and
     * creates a new booking request based on that.
     * @param view The view that fired the event.
     */
    public void onCompleteBookingButtonClick(final View view) {
        final String firstName = ((EditText) findViewById(R.id.guestFirstName)).getText().toString();
        final String lastName = ((EditText) findViewById(R.id.guestLastName)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.guestPhoneNumber)).getText().toString();
        final String email = ((EditText) findViewById(R.id.guestEmail)).getText().toString();

        final String addressLine1 = ((EditText) findViewById(R.id.billingInformationAddress1)).getText().toString();
        final String addressLine2 = ((EditText) findViewById(R.id.billingInformationAddress2)).getText().toString();
        final String city = ((EditText) findViewById(R.id.billingInformationCity)).getText().toString();
        final String state = ((EditText) findViewById(R.id.billingInformationState)).getText().toString();
        final String country = ((EditText) findViewById(R.id.billingInformationCountry)).getText().toString();
        final String zip = ((EditText) findViewById(R.id.billingInformationZip)).getText().toString();

        final String cardType = ((Spinner) findViewById(R.id.billingInformationCCType)).getSelectedItem().toString();
        final String cardNumber = ((EditText) findViewById(R.id.billingInformationCCNum)).getText().toString();
        final String cardExpirationMonth
            = ((Spinner) findViewById(R.id.billingInformationCCExpMo)).getSelectedItem().toString();
        final String cardExpirationYear
            = ((Spinner) findViewById(R.id.billingInformationCCExpYr)).getSelectedItem().toString();
        final String cardSecurityCode
            = ((EditText) findViewById(R.id.billingInformationCCSecurityCode)).getText().toString();


        final int cardExpirationFullYear = Integer.parseInt(cardExpirationYear);
        final int cardExpirationFullMonth = Integer.parseInt(cardExpirationMonth);

        final YearMonth expirationDate = new YearMonth(cardExpirationFullYear, cardExpirationFullMonth);

        final BookingRequest.ReservationInformation reservationInfo = new BookingRequest.ReservationInformation(
            email, firstName, lastName, phone, null, cardType, cardNumber, cardSecurityCode, expirationDate);

        final ReservationRoom reservationRoom = new ReservationRoom(
            reservationInfo.individual.name,
            SampleApp.selectedRoom,
            SampleApp.selectedRoom.bedTypes.get(0).id,
            SampleApp.occupancy());

        final Address reservationAddress
            = new Address(Arrays.asList(addressLine1, addressLine2), city, state, country, zip);

        final BookingRequest request = new BookingRequest(
            SampleApp.selectedHotel.hotelId,
            SampleApp.arrivalDate,
            SampleApp.departureDate,
            SampleApp.selectedHotel.supplierType,
            Collections.singletonList(reservationRoom),
            reservationInfo,
            reservationAddress);


        new BookingRequestTask().execute(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(final int reqCode, final int resultCode, final Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch(reqCode) {
            case PICK_CONTACT_INTENT:
                if (resultCode == Activity.RESULT_OK) {
                    final Uri contactData = data.getData();
                    final String id = getStringForUriQueryAndContactId(
                        getContentResolver(),
                        contactData,
                        null,
                        null,
                        ContactsContract.Contacts._ID);

                    final String[] idAsSelectionArgs = new String[] {id};
                    final String[] displayNamePieces = getStringForUriQueryAndContactId(
                        getContentResolver(),
                        contactData,
                        null,
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME).split(" ");

                    final String firstName = displayNamePieces[0];
                    final String lastName = displayNamePieces[displayNamePieces.length - 1];

                    final String email = getStringForUriQueryAndContactId(
                        getContentResolver(),
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        idAsSelectionArgs,
                        ContactsContract.CommonDataKinds.Email.ADDRESS);

                    final String phoneNumber = getStringForUriQueryAndContactId(
                        getContentResolver(),
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        idAsSelectionArgs,
                        ContactsContract.CommonDataKinds.Phone.NUMBER);

                    final EditText guestFirstName = (EditText) findViewById(R.id.guestFirstName);
                    final EditText guestLastName = (EditText) findViewById(R.id.guestLastName);
                    final EditText guestPhoneNumber = (EditText) findViewById(R.id.guestPhoneNumber);
                    final EditText guestEmail = (EditText) findViewById(R.id.guestEmail);

                    guestFirstName.setText(firstName);
                    guestLastName.setText(lastName);
                    guestPhoneNumber.setText(phoneNumber);
                    guestEmail.setText(email);
                }
        }
    }

    private static String getStringForUriQueryAndContactId(final ContentResolver resolver, final Uri uri,
            final String selection, final String[] selectionArgs, final String columnName) {
        String value = "";
        final Cursor cursor = resolver.query(uri, null, selection, selectionArgs, null, null);
        if (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndex(columnName));
        }
        cursor.close();
        return value;
    }

    /**
     * The task used to actually perform the booking request and pass the returned data off to the next activity.
     */
    private class BookingRequestTask extends AsyncTask<BookingRequest, Void, List<Reservation>> {
        @Override
        protected List<Reservation> doInBackground(final BookingRequest... bookingRequests) {
            final List<Reservation> reservations = new LinkedList<Reservation>();
            for (BookingRequest request : bookingRequests) {
                try {
                    reservations.add(RequestProcessor.run(request));
                } catch (EanWsError ewe) {
                    Log.d(SampleConstants.LOG_TAG, "An APILevel Exception occurred.", ewe);
                } catch (UrlRedirectionException  ure) {
                    SampleApp.sendRedirectionToast(getApplicationContext());
                }
            }
            return reservations;
        }
        @Override
        protected void onPostExecute(final List<Reservation> reservations) {
            super.onPostExecute(reservations);
            for (Reservation reservation : reservations) {
                SampleApp.addReservationToCache(reservation);
            }
            startActivity(new Intent(BookingSummary.this, ReservationDisplay.class));
        }
    }
}