package com.ean.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.request.ListRequest;
import com.ean.mobile.task.ImageTupleLoaderTask;

import java.io.IOException;
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
        ListView hotelListView = ((ListView) findViewById(R.id.HotelList));
        hotelListView.setAdapter(new HotelInfoAdapter(this, R.layout.hotelinfolistlayout));
        hotelListView.setOnScrollListener(new HotelScrollListener());
    }

    private class HotelListAdapterListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SampleApp.selectedHotel = (HotelInfo) parent.getItemAtPosition(position);
            startActivity(new Intent(HotelList.this, HotelFullInfo.class));
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
            }

            //Get the hotel.
            final HotelInfo hotelInfo = this.getItem(position);
            Log.d(SampleConstants.DEBUG, "name " + hotelInfo.name);

            //Set the name field
            final TextView name = (TextView) view.findViewById(R.id.hotelInfoName);
            //TODO: use ellipsize
            name.setText(maxLengthString(hotelInfo.name, MAX_HOTEL_NAME_LEN));

            //Set the short location description
            final TextView locDesc = (TextView) view.findViewById(R.id.hotelInfoLocationDesc);
            //TODO: use ellipsize
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


    private class HotelScrollListener implements AbsListView.OnScrollListener {

        private PerformLoadTask loadingTask;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (view.getLastVisiblePosition() >= SampleApp.foundHotels.size() - 7) {
                if (loadingTask == null || loadingTask.getStatus() == AsyncTask.Status.FINISHED) {
                    loadingTask = new PerformLoadTask((ArrayAdapter) view.getAdapter());
                }
                if (loadingTask.getStatus() == AsyncTask.Status.PENDING) {
                    loadingTask.execute();
                }
            }
        }

        /**
         * no op, implemented for the interface.
         */
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            // see javadoc.
        }
    }

    private class PerformLoadTask extends AsyncTask<Void, Integer, Void> {

        private final ArrayAdapter adapter;

        private PerformLoadTask(ArrayAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ListRequest.loadMoreResults(SampleApp.foundHotels);
            } catch (IOException e) {
                Log.d(SampleConstants.DEBUG, "An IOException occurred while searching for hotels.", e);
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.DEBUG, "An APILevel Exception occurred.", ewe);
            } catch (UrlRedirectionException ure) {
                SampleApp.sendRedirectionToast(getApplicationContext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }
}
