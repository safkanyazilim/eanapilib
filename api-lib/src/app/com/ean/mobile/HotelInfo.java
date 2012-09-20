package com.ean.mobile;

import android.graphics.drawable.Drawable;
import android.text.Html;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class HotelInfo {

    public String name,
                  shortDescription,
                  longDescription,
                  locDescription,
                  starRating,
                  thumbnailUrl,
                  currencyCode,
                  hotelId,
                  address,
                  city,
                  stateProvinceCode,
                  supplierType;

    public BigDecimal highPrice,
                      lowPrice;

    public Drawable thumbnail;

    public boolean hasRetrievedHotelInfo = false,
                   hasRetrievedRoomAvail = false;

    public ArrayList<HotelImageTuple> images;

    public ArrayList<HotelRoom> hotelRooms;

    public HotelInfo(JSONObject hotelSummary) throws JSONException{
        this.name = Html.fromHtml(hotelSummary.optString("name")).toString();
        this.hotelId = hotelSummary.optString("hotelId");
        this.address = hotelSummary.getString("address1");
        this.city = hotelSummary.getString("city");
        this.stateProvinceCode = hotelSummary.getString("stateProvinceCode");
        this.shortDescription =  Html.fromHtml(hotelSummary.optString("shortDescription")).toString();
        this.locDescription = Html.fromHtml(hotelSummary.optString("locationDescription")).toString();
        this.starRating = hotelSummary.isNull("hotelRating") || hotelSummary.getString("hotelRating").equals("")
                                ? "0"
                                : hotelSummary.getString("hotelRating");
        this.thumbnailUrl = hotelSummary.optString("thumbNailUrl").replace("_t.jpg", "_n.jpg");
        this.highPrice = new BigDecimal(hotelSummary.getString("highRate"));
        this.lowPrice = new BigDecimal(hotelSummary.getString("lowRate"));
        this.currencyCode = hotelSummary.optString("rateCurrencyCode");
        this.supplierType = hotelSummary.optString("supplierType");
    }

    public String toString(){
        return this.name;
    }

}
