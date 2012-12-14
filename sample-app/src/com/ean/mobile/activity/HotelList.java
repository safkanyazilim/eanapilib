package com.ean.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.util.Log;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.R;
import com.ean.mobile.SampleApp;
import com.ean.mobile.SampleConstants;
import com.ean.mobile.StarRating;
import com.ean.mobile.task.ImageTupleLoaderTask;

import java.text.NumberFormat;
import java.util.Currency;

public class HotelList extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotellist);
        ((ListView) findViewById(R.id.HotelList)).setOnItemClickListener(new HotelListAdapterListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) findViewById(R.id.searchQuery)).setText(SampleApp.searchQuery);
        ((ListView) findViewById(R.id.HotelList)).setAdapter(new HotelInfoAdapter(this, R.layout.hotelinfolistlayout));
    }

    private class HotelListAdapterListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SampleApp.selectedHotel = (HotelInfo) parent.getItemAtPosition(position);
            Intent intent = new Intent(HotelList.this, HotelFullInfo.class);
            startActivity(intent);
        }
    }

    private static class HotelInfoAdapter extends ArrayAdapter<HotelInfo> {

        private static final String ELLIPSIS = "…";

        private static final int MAX_HOTEL_NAME_LEN = 40;
        private static final int MAX_HOTEL_LOC_LEN = 25;


        private final LayoutInflater layoutInflater;

        /**
         * Overloads the constructor who takes the same parameters plus a list of objects.
         * Uses {@link SampleApp#foundHotels} as the list of objects it will use.
         * @param context The context passed in by the calling class.
         * @param resource The resourceId of the resource this is being applied to.
         */
        private HotelInfoAdapter(Context context, int resource) {
            super(context, resource, SampleApp.foundHotels);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        /**
         * {@inheritDoc}
         */
        public View getView (int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.hotelinfolistlayout, null);
            } else {
                //return v;
            }

            //Get the hotel.
            final HotelInfo hotelInfo = this.getItem(position);
            Log.d(SampleConstants.DEBUG, "name " + hotelInfo.name);

            //Set the name field
            final TextView name = (TextView) view.findViewById(R.id.hotelInfoName);
            name.setText(maxLengthString(hotelInfo.name, MAX_HOTEL_NAME_LEN));

            //Set the short location description
            final TextView locDesc = (TextView) view.findViewById(R.id.hotelInfoLocationDesc);
            locDesc.setText(maxLengthString(hotelInfo.locDescription, MAX_HOTEL_LOC_LEN));

            //Populate the star rating
            StarRating.populate((LinearLayout) view.findViewById(R.id.hotelInfoStars), hotelInfo.starRating);

            //Get and show the pricing information.
            fixUpPricesAndDrr(view, hotelInfo);

            //Get and show the thumbnail image
            final ImageView thumb = (ImageView) view.findViewById(R.id.hotelInfoThumb);
            // reset the thumb to eliminate possible flickering
            thumb.setImageResource(R.drawable.noimg_large);

            if (hotelInfo.mainHotelImageTuple.thumbnailUrl != null) {
                if (hotelInfo.mainHotelImageTuple.isThumbnailLoaded()){
                    thumb.setImageDrawable(hotelInfo.mainHotelImageTuple.getThumbnailImage());
                } else {
                    new ImageTupleLoaderTask(thumb, false).execute(hotelInfo.mainHotelImageTuple);
                }
            }

            //TODO: INCLUDE THE DRRPROMO WHEN APPROPRIATE

            return view;
        }

        /**
         * Makes a string with max length of maxLength - 1 based on base.
         * @param base The base string.
         * @param maxLength The maximum length of the returned string.
         * @return A string which is equal to base, or the substring whose length is 1 less then maxlength.
         */
        private static String maxLengthString(final String base, final int maxLength) {
            if (base != null && base.length() >= maxLength) {
                return base.substring(0, maxLength - 1) + ELLIPSIS;
            }
            return base;
        }

        private static void fixUpPricesAndDrr(View view, HotelInfo hotelInfo) {
            final TextView highPrice = (TextView) view.findViewById(R.id.highPrice);
            final TextView lowPrice = (TextView) view.findViewById(R.id.lowPrice);
            final ImageView drrIcon = (ImageView) view.findViewById(R.id.drrPromoImg);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(Currency.getInstance(hotelInfo.currencyCode));
            lowPrice.setText(currencyFormat.format(hotelInfo.lowPrice));
            if(hotelInfo.lowPrice.equals(hotelInfo.highPrice)){
                highPrice.setVisibility(TextView.GONE);
                drrIcon.setVisibility(ImageView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                drrIcon.setVisibility(ImageView.VISIBLE);
                highPrice.setText(currencyFormat.format(hotelInfo.highPrice));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

    }
}
