package com.ean.mobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
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
import com.ean.mobile.HotelWrangler;
import com.ean.mobile.R;
import com.ean.mobile.StarRating;
import com.ean.mobile.ImageFetcher;
import com.ean.mobile.request.InformationRequest;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Currency;

public class HotelList extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hotellist);

        ListView hotList = (ListView) findViewById(R.id.HotelList);
        hotList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HotelInfo hi = (HotelInfo) parent.getItemAtPosition(position);
                ((HotelWrangler)getApplicationContext()).setSelectedInfo(hi);
                // TODO: set wrangler's check in and out to the checkin and out times for the hotel
                Intent intent = new Intent(HotelList.this, HotelFullInfo.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        TextView queryBox = (TextView) findViewById(R.id.searchQuery);
        HotelWrangler wrangler = (HotelWrangler)getApplicationContext();
        queryBox.setText(wrangler.getSearchQueryDisplay());
        ListView hotList = (ListView) findViewById(R.id.HotelList);
        hotList.setAdapter(getHotelInfoAdapter(this, R.layout.hotelinfolistlayout));
    }



    public ArrayAdapter<HotelInfo> getHotelInfoAdapter(Context context, int resource){
        HotelWrangler wrangler = (HotelWrangler)getApplicationContext();
        return new ArrayAdapter<HotelInfo>(context, resource, wrangler.getInfos()){
            public View getView (int position, View convertView, ViewGroup parent){
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.hotelinfolistlayout, null);
                } else {
                    //return v;
                }
                final TextView name = (TextView) v.findViewById(R.id.hotelInfoName);
                final TextView locDesc = (TextView) v.findViewById(R.id.hotelInfoLocationDesc);
                final TextView highPrice = (TextView) v.findViewById(R.id.highPrice);
                final TextView lowPrice = (TextView) v.findViewById(R.id.lowPrice);
                final ImageView thumb = (ImageView) v.findViewById(R.id.hotelInfoThumb);
                final ImageView drrIcon = (ImageView) v.findViewById(R.id.drrPromoImg);
                final HotelInfo hi = this.getItem(position);

                // reset the thumb to eliminate possible flickering
                thumb.setImageResource(R.drawable.noimg_large);

                final int
                    maxHotelNameLen = 40,
                    maxHotelLocLen = 25;

                String hotelname = hi.name;
                if(hotelname.length() >= maxHotelNameLen){
                    hotelname = hotelname.substring(0,maxHotelNameLen-1) + "…";
                }
                name.setText(hotelname);
                Log.d("EANDebug", "name "+hi.name);
                String locationdescription = hi.locDescription;
                if(locationdescription.length() >= maxHotelLocLen){
                    locationdescription = locationdescription.substring(0,maxHotelLocLen-1) + "…";
                }
                locDesc.setText(locationdescription);

                StarRating.populate((LinearLayout) v.findViewById(R.id.hotelInfoStars), hi.starRating);

                if(hi.hasRetrievedHotelInfo) {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                    currencyFormat.setCurrency(Currency.getInstance(hi.currencyCode));
                    lowPrice.setText(currencyFormat.format(hi.lowPrice));
                    if(hi.lowPrice.equals(hi.highPrice)){
                        highPrice.setVisibility(TextView.GONE);
                        drrIcon.setVisibility(ImageView.GONE);
                    } else {
                        highPrice.setVisibility(TextView.VISIBLE);
                        drrIcon.setVisibility(ImageView.VISIBLE);
                        highPrice.setText(currencyFormat.format(hi.highPrice));
                        highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                } else {
                    // TODO: run the hotelinfo request here
                   Thread requestThread = new Thread(){
                    public void run(){
                        try{
                            Looper.prepare();
                            HotelWrangler wrangler = (HotelWrangler)getApplicationContext();
                            InformationRequest.getHotelInformation(hi, wrangler);
                            runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
                                        currencyFormat.setCurrency(Currency.getInstance(hi.currencyCode));
                                        lowPrice.setText(currencyFormat.format(hi.lowPrice));
                                        if(hi.lowPrice.equals(hi.highPrice)){
                                            highPrice.setVisibility(TextView.GONE);
                                            drrIcon.setVisibility(ImageView.GONE);
                                        } else {
                                            highPrice.setVisibility(TextView.VISIBLE);
                                            drrIcon.setVisibility(ImageView.VISIBLE);
                                            highPrice.setText(currencyFormat.format(hi.highPrice));
                                            highPrice.setPaintFlags(highPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        }
                                    }
                                }
                            );
                        }catch (Exception e) {
                            Log.d("EANDebug", " An exception occurred: " + e.getLocalizedMessage());
                        }
                    }
                    };
                    requestThread.start();
                }

                if(hi.thumbnailUrl != null && hi.thumbnailUrl != ""){
                    if(hi.thumbnail == null){
                        Thread imageThread = new Thread(){
                            public void run(){
                                try {
                                    hi.thumbnail = Drawable.createFromStream(ImageFetcher.fetch(hi.thumbnailUrl), "src");
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

                //TODO: INCLUDE THE DRRPROMO WHEN APPROPRIATE


                return v;
            }
        };

    }
}
