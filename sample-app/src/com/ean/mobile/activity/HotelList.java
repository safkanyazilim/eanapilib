/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.activity;

import java.text.NumberFormat;
import java.util.Currency;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ean.mobile.R;
import com.ean.mobile.app.ImageFetcher;
import com.ean.mobile.app.SampleApp;
import com.ean.mobile.app.SampleConstants;
import com.ean.mobile.app.StarRating;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.request.ListRequest;
import com.ean.mobile.request.RequestProcessor;

/**
 * The code behind the HotelList layout.
 */
public class HotelList extends Activity {

    private Toast loadingMoreHotelsToast;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotellist);
        final ListView hotelListView = (ListView) findViewById(R.id.HotelList);
        hotelListView.setOnItemClickListener(new HotelListAdapterListener());

        loadingMoreHotelsToast = Toast.makeText(getApplicationContext(),
            getString(R.string.loading_more_hotels), Toast.LENGTH_LONG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        ((TextView) findViewById(R.id.searchQuery)).setText(SampleApp.searchQuery);
        final ListView hotelListView = (ListView) findViewById(R.id.HotelList);
        hotelListView.setAdapter(new HotelAdapter(getApplicationContext(), R.layout.hotellistlayout));
        hotelListView.setOnScrollListener(new HotelScrollListener());
    }

    private class HotelListAdapterListener implements AdapterView.OnItemClickListener {
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            SampleApp.selectedHotel = (Hotel) parent.getItemAtPosition(position);
            startActivity(new Intent(HotelList.this, HotelInformation.class));
        }
    }

    private static final class HotelAdapter extends ArrayAdapter<Hotel> {

        private final LayoutInflater layoutInflater;

        /**
         * Overloads the constructor who takes the same parameters plus a list of objects.
         * Uses {@link SampleApp#FOUND_HOTELS} as the list of objects it will use.
         * @param context The context passed in by the calling class.
         * @param resource The resourceId of the resource this is being applied to.
         */
        private HotelAdapter(final Context context, final int resource) {
            super(context, resource, SampleApp.FOUND_HOTELS);
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.hotellistlayout, null);
            }

            //Get the hotel.
            final Hotel hotel = this.getItem(position);
            Log.d(SampleConstants.LOG_TAG, "name " + hotel.name);

            //Set the name field
            final TextView name = (TextView) view.findViewById(R.id.hotelName);
            name.setText(hotel.name);

            //Set the short location description
            final TextView locDesc = (TextView) view.findViewById(R.id.hotelLocationDesc);
            locDesc.setText(hotel.locationDescription);

            //Populate the star rating
            StarRating.populate((LinearLayout) view.findViewById(R.id.hotelStars), hotel.starRating);

            //Get and show the pricing information.
            fixUpPricesAndDrr(view, hotel);

            //Get and show the thumbnail image
            final ImageView thumb = (ImageView) view.findViewById(R.id.hotelThumb);
            // reset the thumb to eliminate possible flickering
            thumb.setImageResource(R.drawable.noimg_large);

            ImageFetcher.loadThumbnailIntoImageView(thumb, hotel.mainHotelImageTuple);

            //TODO: INCLUDE THE DRRPROMO WHEN APPROPRIATE

            return view;
        }

        private static void fixUpPricesAndDrr(final View view, final Hotel hotel) {
            final TextView highPrice = (TextView) view.findViewById(R.id.highPrice);
            final TextView lowPrice = (TextView) view.findViewById(R.id.lowPrice);
            final ImageView drrIcon = (ImageView) view.findViewById(R.id.drrPromoImg);
            final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(Currency.getInstance(hotel.currencyCode));
            lowPrice.setText(currencyFormat.format(hotel.lowPrice));
            if (hotel.lowPrice.equals(hotel.highPrice)) {
                highPrice.setVisibility(TextView.GONE);
                drrIcon.setVisibility(ImageView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                drrIcon.setVisibility(ImageView.VISIBLE);
                highPrice.setText(currencyFormat.format(hotel.highPrice));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }


    private class HotelScrollListener implements AbsListView.OnScrollListener {

        private PerformLoadTask loadingTask;

        private final int distanceFromLastPositionToLoad = 7;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                final int totalItemCount) {
            if (view.getLastVisiblePosition() >= SampleApp.FOUND_HOTELS.size() - distanceFromLastPositionToLoad) {
                if (loadingTask == null || loadingTask.getStatus() == AsyncTask.Status.FINISHED) {
                    loadingTask = new PerformLoadTask((ArrayAdapter) view.getAdapter());
                }
                if (loadingTask.getStatus() == AsyncTask.Status.PENDING) {
                    loadingMoreHotelsToast.show();
                    loadingTask.execute((Void) null);
                }
            }
        }

        /**
         * no op, implemented for the interface.
         * {@inheritDoc}
         */
        @Override
        public void onScrollStateChanged(final AbsListView absListView, final int i) {
            // see javadoc.
        }
    }

    private final class PerformLoadTask extends AsyncTask<Void, Integer, Void> {

        private final ArrayAdapter adapter;

        private PerformLoadTask(final ArrayAdapter adapter) {
            this.adapter = adapter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(final Void... voids) {
            try {
                final ListRequest request = new ListRequest(
                    SampleApp.cacheKey,
                    SampleApp.cacheLocation);

                SampleApp.updateFoundHotels(RequestProcessor.run(request));
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.LOG_TAG, "An APILevel Exception occurred.", ewe);
            } catch (UrlRedirectionException ure) {
                SampleApp.sendRedirectionToast(getApplicationContext());
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(final Void aVoid) {
            adapter.notifyDataSetChanged();
            loadingMoreHotelsToast.cancel();
        }
    }
}
