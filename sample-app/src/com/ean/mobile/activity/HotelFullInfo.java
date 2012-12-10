/**
 * $Copyright: Copyright 2012 EAN.com, L.P. All rights reserved. $
 */
package com.ean.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import com.ean.mobile.HotelImageTuple;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelWrangler;
import com.ean.mobile.R;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.StarRating;
import com.ean.mobile.ImageFetcher;
import com.ean.mobile.request.InformationRequest;
import com.ean.mobile.request.RoomAvailRequest;
import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Currency;

public class HotelFullInfo extends Activity {
    public void onResume() {
        super.onResume();
        setContentView(R.layout.hotelfullinfo);
        Log.d("EANDebug", "starting HotelFullInfo");
        final HotelWrangler wrangler = (HotelWrangler)getApplicationContext();
        final HotelInfo hi = wrangler.getSelectedInfo();
        if(hi != null){
            TextView name = (TextView) this.findViewById(R.id.hotelFullInfoName);
            final WebView description = (WebView) this.findViewById(R.id.hotelFullInfoDescription);
            final TextView address = (TextView) this.findViewById(R.id.hotelFullInfoAddress);
            final ImageView thumb = (ImageView) this.findViewById(R.id.hotelFullInfoThumb);
            final ImageView[] smallThumbs = {
                (ImageView) this.findViewById(R.id.hotelFullInfoSmallThumb00),
                (ImageView) this.findViewById(R.id.hotelFullInfoSmallThumb01),
                (ImageView) this.findViewById(R.id.hotelFullInfoSmallThumb02),
                (ImageView) this.findViewById(R.id.hotelFullInfoSmallThumb10),
                (ImageView) this.findViewById(R.id.hotelFullInfoSmallThumb11),
                (ImageView) this.findViewById(R.id.hotelFullInfoSmallThumb12)
            };
            name.setText(hi.name);

            StarRating.populate((LinearLayout) this.findViewById(R.id.hotelFullInfoStars), hi.starRating);

            if(hi.hasRetrievedRoomAvail) {
                populateRateList();
            } else {
                Thread availThread = new Thread(){
                    public void run() {
                        try {
                            RoomAvailRequest.getRoomAvailForHotel(hi, wrangler);

                            if (wrangler.getSelectedInfo() == null) {
                                // then an error occured and the hotel info was set to null
                                // we should go back to the list.
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO: toast telling the user what happened? handle better?
                                        finish();
                                    }
                                });
                                return;
                            }
                        } catch (IOException ioe) {

                        } catch (JSONException jsoe) {
                            Log.d("EANDebug", jsoe.getMessage());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateRateList();
                            }
                        });
                    }
                };
                availThread.start();
            }


            if(hi.thumbnailUrl != null && !hi.thumbnailUrl.equals("")){
                if(hi.thumbnail == null){
                    Thread imageThread = new Thread(){
                        public void run(){
                            try {
                                hi.thumbnail= Drawable.createFromStream(ImageFetcher.fetch(hi.thumbnailUrl), "src");
                            } catch( IOException ioe) {

                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    thumb.setImageDrawable(hi.thumbnail);
                                }
                            });
                        }
                    };
                    imageThread.start();
                } else {
                    thumb.setImageDrawable(hi.thumbnail);
                }
            }
            if(hi.hasRetrievedHotelInfo){
                address.setText(hi.address);
                description.loadData(hi.longDescription, "text/html", null);
                for(int i = 0; i < smallThumbs.length && i < hi.images.size(); i++){
                    HotelImageTuple thisImage = hi.images.get(i);
                    if(thisImage.thumbnail != null) {
                        smallThumbs[i].setImageDrawable(thisImage.thumbnail);
                    } else if (thisImage.thumbnailUrl != null) {
                        loadThumbnailImage(thisImage, smallThumbs, hi);
                    }
                }
            } else {
                Thread requestThread = new Thread(){
                public void run(){
                    try{
                        Looper.prepare();
                        HotelWrangler wrangler = (HotelWrangler)getApplicationContext();
                        InformationRequest.getHotelInformation(hi, wrangler);
                        runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    address.setText(hi.address);
                                    description.loadData(hi.longDescription, "text/html", null);
                                }
                            }
                        );

                        for(final HotelImageTuple thisImage : hi.images){
                            if(hi.images.indexOf(thisImage) < smallThumbs.length){
                                loadThumbnailImage(thisImage,  smallThumbs,  hi);
                            }
                        }
                    }catch (Exception e) {
                        Log.d("EANDebug", " An exception occurred: " + e.getLocalizedMessage());
                    }
                }
                };
                requestThread.start();
            }


        }else{
            Log.d("EANDebug", "hi was null");
        }
    }

    private void loadThumbnailImage(final HotelImageTuple thisImage, final ImageView[] smallThumbs, final HotelInfo hi) {
        new Thread() {
            public void run(){
                try{
                   // Log.d("EANDebug", thisImage.thumbnailUrl + " loading");
                    if(thisImage.thumbnail == null){
                        thisImage.thumbnail
                        = Drawable.createFromStream(ImageFetcher.fetch(thisImage.thumbnailUrl, true), "src");
                       // Log.d("EANDebug", thisImage.thumbnailUrl + " loaded");
                    }else{
                        //Log.d("EANDebug", thisImage.thumbnailUrl + " already loaded");
                    }
                    if(hi.images.indexOf(thisImage) < smallThumbs.length){
                        runOnUiThread(
                            new Runnable() {
                                public void run() {
                                    int index = hi.images.indexOf(thisImage);
                                    //Log.d("EANDebug", thisImage.thumbnailUrl + " drawing to " + index);
                                    smallThumbs[index].setImageDrawable(thisImage.thumbnail);
                                }
                            }
                        );
                    }
                }catch(IOException e){
                    Log.d("EANDebug", "an exception occurred: " + e.getMessage());
                }
            }
        }.start();
    }

    private void populateRateList(){
        HotelWrangler wrangler = (HotelWrangler)getApplicationContext();
        final HotelInfo hi = wrangler.getSelectedInfo();
        TextView loadingView = (TextView) this.findViewById(R.id.loadingRoomsView);
        loadingView.setVisibility(TextView.GONE);
        if (hi.hotelRooms == null) {
            TextView noAvail = (TextView) this.findViewById(R.id.noRoomsAvailableView);
            noAvail.setVisibility(TextView.VISIBLE);
            return;
        }
        TableLayout rateList = (TableLayout) findViewById(R.id.roomRateList);
        View v = null;
        LayoutInflater vi = null;
        for (HotelRoom room : hi.hotelRooms) {
            vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.roomtypelistlayout, null);
            TextView roomDesc = (TextView) v.findViewById(R.id.roomRateDescritpiton);
            TextView drrPromoText = (TextView) v.findViewById(R.id.drrPromoText);
            TextView highPrice = (TextView) v.findViewById(R.id.highPrice);
            TextView lowPrice = (TextView) v.findViewById(R.id.lowPrice);
            ImageView drrIcon = (ImageView) v.findViewById(R.id.drrPromoImg);

            roomDesc.setText(room.description);

            drrPromoText.setText(room.promoDescription);
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(Currency.getInstance(room.rate.currencyCode));
            lowPrice.setText(currencyFormat.format(room.rate.getAverageRate()));
            if(room.rate.areAverageRatesEqual()){
                highPrice.setVisibility(TextView.GONE);
                drrIcon.setVisibility(ImageView.GONE);
                drrPromoText.setVisibility(ImageView.GONE);
            } else {
                highPrice.setVisibility(TextView.VISIBLE);
                drrIcon.setVisibility(ImageView.VISIBLE);
                drrPromoText.setVisibility(ImageView.VISIBLE);
                highPrice.setText(currencyFormat.format(room.rate.getAverageBaseRate()));
                highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            v.setTag(room);
            v.setClickable(true);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((HotelWrangler)getApplicationContext()).setSelectedRoom((HotelRoom) view.getTag());
                    Intent intent = new Intent(HotelFullInfo.this, BookingSummary.class);
                    startActivity(intent);
                }
            });
            rateList.addView(v);
        }
    }
}