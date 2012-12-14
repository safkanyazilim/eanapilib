package com.ean.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.NightlyRate;
import com.ean.mobile.R;
import com.ean.mobile.SampleApp;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.Currency;

public class BookingSummary extends Activity {

    private static final String DATE_FORMAT_STRING = "EEEE, MMMM dd, yyyy";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT_STRING);

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
        final HotelInfo hotelInfo = SampleApp.selectedHotel;
        HotelRoom hotelRoom = SampleApp.selectedRoom;

        hotelName.setText(hotelInfo.name);
        checkIn.setText(DATE_TIME_FORMATTER.print(SampleApp.arrivalDate));
        checkOut.setText(DATE_TIME_FORMATTER.print(SampleApp.departureDate));
        roomType.setText(hotelRoom.description);
        numGuests.setText(String.format("%d Adult(s), %d Child(ren)", SampleApp.numberOfAdults, SampleApp.numberOfChildren));
        drrPromoText.setText(hotelRoom.promoDescription);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(Currency.getInstance(hotelInfo.currencyCode));

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
            totalHighPrice.setText(currencyFormat.format(hotelInfo.highPrice));
            totalHighPrice.setPaintFlags(totalHighPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        View view;
        LayoutInflater inflater;
        DateTimeFormatter nightlyRateFormatter = DateTimeFormat.forPattern("MM-dd-yyyy");

        DateTime currentDate = SampleApp.arrivalDate.minusDays(1);
        for (NightlyRate rate : SampleApp.selectedRoom.rate.nightlyRates) {
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.pricebreakdownlayout, null);
            TextView date = (TextView) view.findViewById(R.id.priceBreakdownDate);
            TextView highPrice = (TextView) view.findViewById(R.id.priceBreakdownHighPrice);
            TextView lowPrice = (TextView) view.findViewById(R.id.priceBreakdownLowPrice);

            currentDate = currentDate.plusDays(1);
            date.setText(nightlyRateFormatter.print(currentDate));

            currencyFormat.setCurrency(Currency.getInstance(hotelInfo.currencyCode));
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
}