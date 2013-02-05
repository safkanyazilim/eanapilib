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
import android.widget.Toast;
import com.ean.mobile.Hotel;
import com.ean.mobile.ImageFetcher;
import com.ean.mobile.R;
import com.ean.mobile.SampleApp;
import com.ean.mobile.SampleConstants;
import com.ean.mobile.StarRating;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.request.ListRequest;
import com.ean.mobile.request.RequestProcessor;

import java.text.NumberFormat;
import java.util.Currency;

public class HotelList extends Activity {

    private Toast loadingMoreHotelsToast;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotellist);
        ListView hotelListView = (ListView) findViewById(R.id.HotelList);
        hotelListView.setOnItemClickListener(new HotelListAdapterListener());

        loadingMoreHotelsToast = Toast.makeText(getApplicationContext(),
            getString(R.string.loading_more_hotels), Toast.LENGTH_LONG);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) findViewById(R.id.searchQuery)).setText(SampleApp.searchQuery);
        ListView hotelListView = ((ListView) findViewById(R.id.HotelList));
        hotelListView.setAdapter(new HotelInfoAdapter(getApplicationContext(), R.layout.hotellistlayout));
        hotelListView.setOnScrollListener(new HotelScrollListener());
    }

    private class HotelListAdapterListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SampleApp.selectedHotel = (Hotel) parent.getItemAtPosition(position);
            startActivity(new Intent(HotelList.this, HotelInformation.class));
        }
    }

    private static class HotelInfoAdapter extends ArrayAdapter<Hotel> {

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
                view = layoutInflater.inflate(R.layout.hotellistlayout, null);
            }

            //Get the hotel.
            final Hotel hotel = this.getItem(position);
            Log.d(SampleConstants.DEBUG, "name " + hotel.name);

            //Set the name field
            final TextView name = (TextView) view.findViewById(R.id.hotelName);
            name.setText(hotel.name);

            //Set the short location description
            final TextView locDesc = (TextView) view.findViewById(R.id.hotelLocationDesc);
            locDesc.setText(hotel.locDescription);

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

        private static void fixUpPricesAndDrr(View view, Hotel hotel) {
            final TextView highPrice = (TextView) view.findViewById(R.id.highPrice);
            final TextView lowPrice = (TextView) view.findViewById(R.id.lowPrice);
            final ImageView drrIcon = (ImageView) view.findViewById(R.id.drrPromoImg);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(Currency.getInstance(hotel.currencyCode));
            lowPrice.setText(currencyFormat.format(hotel.lowPrice));
            if(hotel.lowPrice.equals(hotel.highPrice)){
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

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (view.getLastVisiblePosition() >= SampleApp.foundHotels.size() - 7) {
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
                ListRequest request = new ListRequest(
                    SampleApp.locale.toString(),
                    SampleApp.currency.getCurrencyCode(),
                    SampleApp.cacheKey,
                    SampleApp.cacheLocation,
                    SampleApp.customerSessionId);

                SampleApp.updateFoundHotels(RequestProcessor.run(request));
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
            loadingMoreHotelsToast.cancel();
        }
    }
}
