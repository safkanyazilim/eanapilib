package com.ean.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.HotelWrangler;
import com.ean.mobile.NightlyRate;
import com.ean.mobile.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

public class BookingSummary extends Activity {
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
        HotelWrangler wrangler = (HotelWrangler) this.getApplicationContext();
        final HotelInfo hi = ((HotelWrangler) this.getApplicationContext()).getSelectedInfo();
        HotelRoom rrd = ((HotelWrangler) this.getApplicationContext()).getSelectedRoom();

        hotelName.setText(hi.name);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        checkIn.setText(dateFormat.format(wrangler.getArrivalDate().getTime()));
        checkOut.setText(dateFormat.format(wrangler.getDepartureDate().getTime()));
        roomType.setText(rrd.description);
        numGuests.setText(String.format("%d Adult, %d Children", wrangler.getNumberOfAdults(), wrangler.getNumberOfChildren()));
        drrPromoText.setText(rrd.promoDescription);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        currencyFormat.setCurrency(Currency.getInstance(hi.currencyCode));

        taxesAndFees.setText(currencyFormat.format(rrd.getTaxesAndFees()));

        totalLowPrice.setText(currencyFormat.format(rrd.getTotalRate()));
        if(rrd.getTotalRate().equals(rrd.getTotalBaseRate())){
            totalHighPrice.setVisibility(TextView.GONE);
            drrIcon.setVisibility(ImageView.GONE);
            drrPromoText.setVisibility(ImageView.GONE);
        } else {
            totalHighPrice.setVisibility(TextView.VISIBLE);
            drrIcon.setVisibility(ImageView.VISIBLE);
            drrPromoText.setVisibility(ImageView.VISIBLE);
            totalHighPrice.setText(currencyFormat.format(hi.highPrice));
            totalHighPrice.setPaintFlags(totalHighPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        Log.d()
        View v = null;
        LayoutInflater vi = null;
        for (NightlyRate rate : rrd.rate) {
            vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.pricebreakdownlayout, null);
            TextView date = (TextView) v.findViewById(R.id.priceBreakdownDate);
            TextView highPrice = (TextView) v.findViewById(R.id.priceBreakdownHighPrice);
            TextView lowPrice = (TextView) v.findViewById(R.id.priceBreakdownLowPrice);

            Date thisDate
                = new Date(wrangler.getArrivalDate().getTime()
                           + 24L*60L*60L*1000*rrd.rate.indexOf(rate));
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            date.setText(format.format(thisDate));

            currencyFormat.setCurrency(Currency.getInstance(hi.currencyCode));
            lowPrice.setText(currencyFormat.format(rate.rate));
            if(rate.rate.equals(rate.baseRate)){
                highPrice.setVisibility(TextView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                highPrice.setText(currencyFormat.format(rate.baseRate));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            priceBreakdownList.addView(v);
        }
    }
}