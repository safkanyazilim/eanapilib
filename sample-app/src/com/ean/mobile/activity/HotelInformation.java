/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
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

import com.ean.mobile.R;
import com.ean.mobile.app.HotelImageDrawable;
import com.ean.mobile.app.ImageFetcher;
import com.ean.mobile.app.SampleApp;
import com.ean.mobile.app.SampleConstants;
import com.ean.mobile.app.StarRating;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelRoom;
import com.ean.mobile.hotel.request.InformationRequest;
import com.ean.mobile.hotel.request.RoomAvailabilityRequest;
import com.ean.mobile.request.RequestProcessor;
import com.ean.mobile.task.ImageDrawableLoaderTask;

/**
 * The code behind the HotelInformation layout.
 */
public class HotelInformation extends Activity {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.hotelinformation);
        Log.d(SampleConstants.LOG_TAG, "starting HotelInformation");
        final Hotel hotel = SampleApp.selectedHotel;

        if (hotel == null) {
            Log.d(SampleConstants.LOG_TAG, "hotel info was null");
            return;
        }

        final TextView name = (TextView) this.findViewById(R.id.hotelInformationName);
        name.setText(hotel.name);

        StarRating.populate((LinearLayout) this.findViewById(R.id.hotelInformationStars), hotel.starRating);

        if (SampleApp.HOTEL_ROOMS.containsKey(hotel.hotelId)) {
            populateRateList();
        } else {
            new AvailabilityInformationLoaderTask(
                hotel.hotelId, SampleApp.arrivalDate, SampleApp.departureDate).execute((Void) null);
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
        final com.ean.mobile.hotel.HotelInformation hotelInformation
                = SampleApp.EXTENDED_INFOS.get(SampleApp.selectedHotel.hotelId);
        description.loadData(hotelInformation.longDescription, "text/html", null);
        for (int i = 0; i < smallThumbs.length && i < hotelInformation.images.size(); i++) {
            final HotelImageDrawable thisDrawable = SampleApp.IMAGE_DRAWABLES.get(hotelInformation.images.get(i));
            if (thisDrawable.isThumbnailLoaded()) {
                smallThumbs[i].setImageDrawable(thisDrawable.getThumbnailImage());
            } else {
                new ImageDrawableLoaderTask(smallThumbs[i], false).execute(thisDrawable);
            }
        }
    }

    private void populateRateList() {
        final Hotel hotel = SampleApp.selectedHotel;
        final TextView loadingView = (TextView) this.findViewById(R.id.loadingRoomsView);
        loadingView.setVisibility(TextView.GONE);
        if (!SampleApp.HOTEL_ROOMS.containsKey(hotel.hotelId)) {
            final TextView noAvail = (TextView) this.findViewById(R.id.noRoomsAvailableView);
            noAvail.setVisibility(TextView.VISIBLE);
            return;
        }
        final TableLayout rateList = (TableLayout) findViewById(R.id.roomRateList);
        View view;
        final LayoutInflater inflater = getLayoutInflater();
        for (HotelRoom room : SampleApp.HOTEL_ROOMS.get(hotel.hotelId)) {
            view = inflater.inflate(R.layout.roomtypelistlayout, null);

            final TextView roomDesc = (TextView) view.findViewById(R.id.roomRateDescritpiton);
            roomDesc.setText(room.description);

            final TextView drrPromoText = (TextView) view.findViewById(R.id.drrPromoText);
            drrPromoText.setText(room.promoDescription);

            final TextView highPrice = (TextView) view.findViewById(R.id.highPrice);
            final TextView lowPrice = (TextView) view.findViewById(R.id.lowPrice);
            final ImageView drrIcon = (ImageView) view.findViewById(R.id.drrPromoImg);
            final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
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
                public void onClick(final View view) {
                    SampleApp.selectedRoom = (HotelRoom) view.getTag();
                    startActivity(new Intent(HotelInformation.this, BookingSummary.class));
                }
            });
            rateList.addView(view);
        }
    }

    private class AvailabilityInformationLoaderTask extends AsyncTask<Void, Void, List<HotelRoom>> {
        private final long hotelId;
        private final LocalDate arrivalDate;
        private final LocalDate departureDate;

        public AvailabilityInformationLoaderTask(final long hotelId,
                final LocalDate arrivalDate, final LocalDate departureDate) {
            this.hotelId = hotelId;
            this.arrivalDate = arrivalDate;
            this.departureDate = departureDate;
        }


        @Override
        protected List<HotelRoom> doInBackground(final Void... voids) {
            try {
                final RoomAvailabilityRequest request
                    = new RoomAvailabilityRequest(hotelId, SampleApp.occupancy(), arrivalDate, departureDate);
                return RequestProcessor.run(request);
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.LOG_TAG, "An error occurred in the api", ewe);
            } catch (UrlRedirectionException ure) {
                SampleApp.sendRedirectionToast(getApplicationContext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<HotelRoom> hotelRooms) {
            super.onPostExecute(hotelRooms);
            SampleApp.HOTEL_ROOMS.put(hotelId, hotelRooms);
            populateRateList();
        }
    }

    private class ExtendedInformationLoaderTask
            extends AsyncTask<Void, Integer, com.ean.mobile.hotel.HotelInformation> {

        private final long hotelId;

        public ExtendedInformationLoaderTask(final long hotelId) {
            this.hotelId = hotelId;
        }

        @Override
        protected com.ean.mobile.hotel.HotelInformation doInBackground(final Void... voids) {
            try {
                return RequestProcessor.run(new InformationRequest(hotelId));
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.LOG_TAG, "Unexpected error occurred within the api", ewe);
            } catch (UrlRedirectionException ure) {
                SampleApp.sendRedirectionToast(getApplicationContext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final com.ean.mobile.hotel.HotelInformation hotelInformation) {
            SampleApp.EXTENDED_INFOS.put(hotelId, hotelInformation);
            setExtendedInfoFields();
        }
    }
}