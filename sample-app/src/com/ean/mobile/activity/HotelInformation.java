/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.activity;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

import org.joda.time.LocalDate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ean.mobile.HotelImageDrawable;
import com.ean.mobile.ImageFetcher;
import com.ean.mobile.R;
import com.ean.mobile.SampleApp;
import com.ean.mobile.SampleConstants;
import com.ean.mobile.StarRating;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.request.InformationRequest;
import com.ean.mobile.hotel.request.RoomAvailabilityRequest;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.task.ImageDrawableLoaderTask;

public class HotelInformation extends Activity {
    public void onResume() {
        super.onResume();
        setContentView(R.layout.hotelinformation);
        Log.d(SampleConstants.DEBUG, "starting HotelInformation");
        final Hotel hotel = SampleApp.selectedHotel;

        if (hotel == null) {
            Log.d(SampleConstants.DEBUG, "hotel info was null");
            return;
        }

        TextView name = (TextView) this.findViewById(R.id.hotelInformationName);
        name.setText(hotel.name);

        StarRating.populate((LinearLayout) this.findViewById(R.id.hotelInformationStars), hotel.starRating);

        if (SampleApp.HOTEL_ROOMS.containsKey(hotel.hotelId)) {
            populateRateList();
        } else {
            new AvailabilityInformationLoaderTask(
                hotel.hotelId, SampleApp.numberOfAdults, SampleApp.numberOfChildren,
                SampleApp.arrivalDate, SampleApp.departureDate).execute((Void) null);
        }

        ImageFetcher.loadThumbnailIntoImageView(
            (ImageView) findViewById(R.id.hotelInformationThumb),
            hotel.mainHotelImageTuple);

        if (SampleApp.EXTENDED_INFOS.containsKey(hotel.hotelId)) {
            setExtendedInfoFields();
        } else {
            new ExtendedInformationLoaderTask(hotel.hotelId).execute((Void) null);
        }
    }

    private void setExtendedInfoFields() {
        final WebView description = (WebView) this.findViewById(R.id.hotelInformationDescription);
        final TextView address = (TextView) this.findViewById(R.id.hotelInformationAddress);
        final ImageView[] smallThumbs = {
            (ImageView) this.findViewById(R.id.hotelInformationSmallThumb00),
            (ImageView) this.findViewById(R.id.hotelInformationSmallThumb01),
            (ImageView) this.findViewById(R.id.hotelInformationSmallThumb02),
            (ImageView) this.findViewById(R.id.hotelInformationSmallThumb10),
            (ImageView) this.findViewById(R.id.hotelInformationSmallThumb11),
            (ImageView) this.findViewById(R.id.hotelInformationSmallThumb12)
        };

        address.setText(SampleApp.selectedHotel.address.toString());
        com.ean.mobile.hotel.HotelInformation hotelInformation = SampleApp.EXTENDED_INFOS.get(SampleApp.selectedHotel.hotelId);
        description.loadData(hotelInformation.longDescription, "text/html", null);
        for (int i = 0; i < smallThumbs.length && i < hotelInformation.images.size(); i++) {
            HotelImageDrawable thisDrawable = SampleApp.IMAGE_DRAWABLES.get(hotelInformation.images.get(i));
            if (thisDrawable.isThumbnailLoaded()) {
                smallThumbs[i].setImageDrawable(thisDrawable.getThumbnailImage());
            } else {
                new ImageDrawableLoaderTask(smallThumbs[i], false).execute(thisDrawable);
            }
        }
    }

    private class AvailabilityInformationLoaderTask extends AsyncTask<Void, Void, List<HotelRoom>> {
        private final long hotelId;
        private final int numberOfAdults;
        private final int numberOfChildren;
        private final LocalDate arrivalDate;
        private final LocalDate departureDate;

        public AvailabilityInformationLoaderTask(final long hotelId,
                final int numberOfAdults,  final int numberOfChildren,
                final LocalDate arrivalDate, final LocalDate departureDate) {
            this.hotelId = hotelId;
            this.numberOfAdults = numberOfAdults;
            this.numberOfChildren = numberOfChildren;
            this.arrivalDate = arrivalDate;
            this.departureDate = departureDate;
        }


        @Override
        protected List<HotelRoom> doInBackground(Void... voids) {
            try {
                RoomAvailabilityRequest request
                    = new RoomAvailabilityRequest(hotelId, SampleApp.occupancy(), arrivalDate, departureDate);
                return RequestProcessor.run(request);
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.DEBUG, "An error occurred in the api", ewe);
            } catch (UrlRedirectionException ure) {
                SampleApp.sendRedirectionToast(getApplicationContext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<HotelRoom> hotelRooms) {
            super.onPostExecute(hotelRooms);
            SampleApp.HOTEL_ROOMS.put(hotelId, hotelRooms);
            populateRateList();
        }
    }

    private class ExtendedInformationLoaderTask
            extends AsyncTask<Void, Integer, com.ean.mobile.hotel.HotelInformation> {

        private final long hotelId;

        public ExtendedInformationLoaderTask(long hotelId) {
            this.hotelId = hotelId;
        }

        @Override
        protected com.ean.mobile.hotel.HotelInformation doInBackground(Void... voids) {
            try {
                InformationRequest request = new InformationRequest(hotelId);
                return RequestProcessor.run(request);
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.DEBUG, "Unexpected error occurred within the api", ewe);
            } catch (UrlRedirectionException ure) {
                SampleApp.sendRedirectionToast(getApplicationContext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(com.ean.mobile.hotel.HotelInformation hotelInformation) {
            SampleApp.EXTENDED_INFOS.put(hotelId, hotelInformation);
            setExtendedInfoFields();
        }
    }

    private void populateRateList() {
        final Hotel hotel = SampleApp.selectedHotel;
        TextView loadingView = (TextView) this.findViewById(R.id.loadingRoomsView);
        loadingView.setVisibility(TextView.GONE);
        if (!SampleApp.HOTEL_ROOMS.containsKey(hotel.hotelId)) {
            TextView noAvail = (TextView) this.findViewById(R.id.noRoomsAvailableView);
            noAvail.setVisibility(TextView.VISIBLE);
            return;
        }
        TableLayout rateList = (TableLayout) findViewById(R.id.roomRateList);
        View view;
        final LayoutInflater inflater = getLayoutInflater();
        for (HotelRoom room : SampleApp.HOTEL_ROOMS.get(hotel.hotelId)) {
            view = inflater.inflate(R.layout.roomtypelistlayout, null);

            TextView roomDesc = (TextView) view.findViewById(R.id.roomRateDescritpiton);
            roomDesc.setText(room.description);

            TextView drrPromoText = (TextView) view.findViewById(R.id.drrPromoText);
            drrPromoText.setText(room.promoDescription);

            TextView highPrice = (TextView) view.findViewById(R.id.highPrice);
            TextView lowPrice = (TextView) view.findViewById(R.id.lowPrice);
            ImageView drrIcon = (ImageView) view.findViewById(R.id.drrPromoImg);
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(Currency.getInstance(room.rate.chargeable.currencyCode));
            lowPrice.setText(currencyFormat.format(room.rate.chargeable.getAverageRate()));
            if (room.rate.chargeable.areAverageRatesEqual()) {
                highPrice.setVisibility(TextView.GONE);
                drrIcon.setVisibility(ImageView.GONE);
                drrPromoText.setVisibility(ImageView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                drrIcon.setVisibility(ImageView.VISIBLE);
                drrPromoText.setVisibility(ImageView.VISIBLE);
                highPrice.setText(currencyFormat.format(room.rate.chargeable.getAverageBaseRate()));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            view.setTag(room);
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                SampleApp.selectedRoom = (HotelRoom) view.getTag();
                Intent intent = new Intent(HotelInformation.this, BookingSummary.class);
                startActivity(intent);
                }
            });
            rateList.addView(view);
        }
    }
}