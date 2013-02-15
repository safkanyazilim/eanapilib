/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.ean.mobile.Address;
import com.ean.mobile.BasicAddress;
import com.ean.mobile.R;
import com.ean.mobile.SampleApp;
import com.ean.mobile.SampleConstants;
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
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

public class BookingSummary extends Activity {

    private static final String DATE_FORMAT_STRING = "EEEE, MMMM dd, yyyy";
    private static final String NIGHTLY_RATE_FORMAT_STRING = "MM-dd-yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);
    private static final DateTimeFormatter NIGHTLY_RATE_FORMATTER = DateTimeFormat.forPattern(NIGHTLY_RATE_FORMAT_STRING);
    private static final int PICK_CONTACT_INTENT = 1;
    private static final int SET_DEFAULT_CREDIT_CARD_INTENT = 2;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.bookingsummary);

        TextView hotelName = (TextView) findViewById(R.id.hotelName);
        TextView checkIn = (TextView) findViewById(R.id.arrivalDisplay);
        TextView checkOut = (TextView) findViewById(R.id.departureDisplay);
        TextView numGuests = (TextView) findViewById(R.id.guestsNumberDisplay);
        TextView roomType = (TextView) findViewById(R.id.roomTypeDisplay);
        TextView bedType = (TextView) findViewById(R.id.bedTypeDisplay);
        TextView taxesAndFees = (TextView) findViewById(R.id.taxes_and_fees_display);
        TextView totalHighPrice = (TextView) findViewById(R.id.highPrice);
        TextView totalLowPrice = (TextView) findViewById(R.id.lowPrice);
        TextView drrPromoText = (TextView) findViewById(R.id.drrPromoText);
        LinearLayout priceBreakdownList = (LinearLayout) findViewById(R.id.priceDetailsBreakdown);
        ImageView drrIcon = (ImageView) findViewById(R.id.drrPromoImg);
        final Hotel hotel = SampleApp.selectedHotel;
        HotelRoom hotelRoom = SampleApp.selectedRoom;
        RoomOccupancy occupancy = hotelRoom.rate.roomGroup.get(0).occupancy;

        hotelName.setText(hotel.name);
        checkIn.setText(DATE_TIME_FORMATTER.print(SampleApp.arrivalDate));
        checkOut.setText(DATE_TIME_FORMATTER.print(SampleApp.departureDate));
        numGuests.setText(String.format(getString(R.string.adults_comma_children), occupancy.numberOfAdults, occupancy.childAges.size()));
        roomType.setText(hotelRoom.description);
        bedType.setText(hotelRoom.bedTypes.get(0).description);
        drrPromoText.setText(hotelRoom.promoDescription);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(Currency.getInstance(hotel.currencyCode));

        taxesAndFees.setText(currencyFormat.format(hotelRoom.getTaxesAndFees()));

        totalLowPrice.setText(currencyFormat.format(hotelRoom.getTotalRate()));
        if(hotelRoom.getTotalRate().equals(hotelRoom.getTotalBaseRate())){
            // if there's no promo, then we make the promo stuff disappear.
            totalHighPrice.setVisibility(TextView.GONE);
            drrIcon.setVisibility(ImageView.GONE);
            drrPromoText.setVisibility(ImageView.GONE);
        } else {
            // if there is a promo, we make it show up.
            totalHighPrice.setVisibility(TextView.VISIBLE);
            drrIcon.setVisibility(ImageView.VISIBLE);
            drrPromoText.setVisibility(ImageView.VISIBLE);
            totalHighPrice.setText(currencyFormat.format(hotel.highPrice));
            totalHighPrice.setPaintFlags(totalHighPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        View view;
        final LayoutInflater inflater = getLayoutInflater();

        LocalDate currentDate = SampleApp.arrivalDate.minusDays(1);
        for (NightlyRate rate : SampleApp.selectedRoom.rate.chargeable.nightlyRates) {
            view = inflater.inflate(R.layout.pricebreakdownlayout, null);
            TextView date = (TextView) view.findViewById(R.id.priceBreakdownDate);
            TextView highPrice = (TextView) view.findViewById(R.id.priceBreakdownHighPrice);
            TextView lowPrice = (TextView) view.findViewById(R.id.priceBreakdownLowPrice);

            currentDate = currentDate.plusDays(1);
            date.setText(NIGHTLY_RATE_FORMATTER.print(currentDate));

            currencyFormat.setCurrency(Currency.getInstance(hotel.currencyCode));
            lowPrice.setText(currencyFormat.format(rate.rate));
            if(rate.rate.equals(rate.baseRate)){
                highPrice.setVisibility(TextView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                highPrice.setText(currencyFormat.format(rate.baseRate));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            priceBreakdownList.addView(view);
        }
    }

    public void onContactChooseButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_INTENT);
    }

    public void onLoadDefaultBillingInfoClick(View view) {
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

        //sorry, but it's just so simple
        addressLine1.setText("travelnow");
        addressLine2.setText("");
        city.setText("Seattle");
        state.setText("WA");
        country.setText("US");
        zip.setText("98004");
        cardType.setSelection(Arrays.asList(getResources().getStringArray(R.array.supported_credit_cards)).indexOf("CA"));
        cardNum.setText("5401999999999999");
        cardExpirationMonth.setText("01");
        cardExpirationYear.setText(Integer.toString((YearMonth.now().getYear() + 1) % 100));
        cardSecurityCode.setText("123");

    }

    public void onCompleteBookingButtonClick(View view) {
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
        final String cardExpirationMonth = ((EditText) findViewById(R.id.billingInformationCCExpMo)).getText().toString();
        final String cardExpirationYear = ((EditText) findViewById(R.id.billingInformationCCExpYr)).getText().toString();
        final String cardSecurityCode = ((EditText) findViewById(R.id.billingInformationCCSecurityCode)).getText().toString();

        //TODO: handle bad user input here.
        final YearMonth expirationDate = new YearMonth(Integer.parseInt(cardExpirationYear.length() == 2 ? "20" + cardExpirationYear : cardExpirationYear), Integer.parseInt(cardExpirationMonth));


        final BookingRequest.ReservationInformation reservationInfo = new BookingRequest.ReservationInformation(email, firstName, lastName, phone, null, cardType, cardNumber, cardSecurityCode, expirationDate);
        final ReservationRoom reservationRoom = new ReservationRoom(reservationInfo.individual.name, SampleApp.selectedRoom, SampleApp.selectedRoom.bedTypes.get(0).id ,SampleApp.occupancy());
        final Address reservationAddress = new BasicAddress(Arrays.asList(addressLine1, addressLine2), city, state, country, zip);

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

    public class BookingRequestTask extends AsyncTask<BookingRequest, Void, List<Reservation>> {
        @Override
        protected List<Reservation> doInBackground(BookingRequest... bookingRequests) {
            List<Reservation> reservations = new LinkedList<Reservation>();
            for (BookingRequest request : bookingRequests) {
                try {
                    reservations.add(RequestProcessor.run(request));
                } catch (EanWsError ewe) {
                    Log.d(SampleConstants.DEBUG, "An APILevel Exception occurred.", ewe);
                } catch (UrlRedirectionException  ure) {
                    SampleApp.sendRedirectionToast(getApplicationContext());
                }
            }
            return reservations;
        }

        @Override
        protected void onPostExecute(List<Reservation> reservations) {
            super.onPostExecute(reservations);
            for (Reservation reservation : reservations) {
                SampleApp.addReservationToCache(getApplicationContext(), reservation);
            }
            startActivity(new Intent(BookingSummary.this, ReservationDisplay.class));
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);

        switch(reqCode) {
            case (PICK_CONTACT_INTENT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    String id = getStringForUriQueryAndContactId(getContentResolver(), contactData, null, null, ContactsContract.Contacts._ID);
                    String[] idAsSelectionArgs = new String[] {id};
                    String[] displayNamePieces = getStringForUriQueryAndContactId(getContentResolver(), contactData, null, null, ContactsContract.Contacts.DISPLAY_NAME).split(" ");
                    String firstName = displayNamePieces[0];
                    String lastName = displayNamePieces[displayNamePieces.length - 1];

                    String email = getStringForUriQueryAndContactId(getContentResolver(), ContactsContract.CommonDataKinds.Email.CONTENT_URI, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", idAsSelectionArgs, ContactsContract.CommonDataKinds.Email.ADDRESS);
                    String phoneNumber = getStringForUriQueryAndContactId(getContentResolver(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", idAsSelectionArgs, ContactsContract.CommonDataKinds.Phone.NUMBER);

                    EditText guestFirstName = (EditText) findViewById(R.id.guestFirstName);
                    EditText guestLastName = (EditText) findViewById(R.id.guestLastName);
                    EditText guestPhoneNumber = (EditText) findViewById(R.id.guestPhoneNumber);
                    EditText guestEmail = (EditText) findViewById(R.id.guestEmail);

                    guestFirstName.setText(firstName);
                    guestLastName.setText(lastName);
                    guestPhoneNumber.setText(phoneNumber);
                    guestEmail.setText(email);
                }
        }
    }

    private static String getStringForUriQueryAndContactId(ContentResolver resolver, Uri uri, String selection, String[] selectionArgs, String columnName) {
        String value = "";
        Cursor cursor = resolver.query(uri, null, selection, selectionArgs, null, null);
        if(cursor.moveToNext()){
            value = cursor.getString(cursor.getColumnIndex(columnName));
        }
        cursor.close();
        return value;
    }
}