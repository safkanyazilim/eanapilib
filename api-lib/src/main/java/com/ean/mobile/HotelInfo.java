/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.text.Html;

/**
 * The holder for information about a particular hotel.
 */
public final class HotelInfo {

    /**
     * The name of this hotel.
     */
    public final String name;

    /**
     * The short description of this hotel.
     */
    public final String shortDescription;

    /**
     * The long description of this hotel.
     */
    public String longDescription;

    /**
     * The location description of this hotel.
     */
    public final String locDescription;

    /**
     * The star rating of this hotel.
     */
    public final String starRating;

    /**
     * The url of the thumbnail of this hotel.
     */
    public final URL thumbnailUrl;

    /**
     * The currency code used for the price of this hotel.
     */
    public final String currencyCode;

    /**
     * The ean id of this hotel.
     */
    public final long hotelId;

    /**
     * The street address of this hotel.
     */
    public final String address;

    /**
     * The city in which the hotel exists.
     */
    public final String city;

    /**
     * The state/province code for the hotel.
     */
    public final String stateProvinceCode;

    /**
     * The type of supplier for the hotel.
     */
    public final String supplierType;

    /**
     * The high price of the hotel, in the currency specified by {currencyCode}.
     */
    public BigDecimal highPrice;

    /**
     * The low price found for the hotel, in the currency specified by {currencyCode}.
     */
    public BigDecimal lowPrice;

    /**
     * Whether or not the full hotel's information has been retrieved.
     */
    public boolean hasRetrievedHotelInfo;


    /**
     * The list of hotel images for this hotel.
     */
    public List<HotelImageTuple> images;

    /**
     * The list of hotel rooms for this hotel.
     */
    public List<HotelRoom> hotelRooms;

    /**
     * The drawable resource for this hotel's thumbnail.
     */
    private Drawable thumbnail;

    /**
     * The constructor that constructs the hotel info from a JSONObject.
     * @param hotelSummary The object holding the hotel's info.
     * @throws JSONException If there is a problem with the JSON objects
     * @throws MalformedURLException If the thumbnail url is not correctly formatted.
     */
    public HotelInfo(final JSONObject hotelSummary) throws JSONException, MalformedURLException {
        this.name = Html.fromHtml(hotelSummary.optString("name")).toString();
        this.hotelId = hotelSummary.optLong("hotelId");
        this.address = hotelSummary.getString("address1");
        this.city = hotelSummary.getString("city");
        this.stateProvinceCode = hotelSummary.optString("stateProvinceCode");
        this.shortDescription =  Html.fromHtml(hotelSummary.optString("shortDescription")).toString();
        this.locDescription = Html.fromHtml(hotelSummary.optString("locationDescription")).toString();
        final String starRatingRaw = hotelSummary.optString("hotelRating");
        this.starRating = null == starRatingRaw || "".equals(starRatingRaw) ? "0" : starRatingRaw;
        final String thumbnailString = hotelSummary.optString("thumbNailUrl").replace("_t.jpg", "_n.jpg");
        this.thumbnailUrl = ImageFetcher.getFullImageUrl(thumbnailString);
        //TODO: figure a better way of scaling the rates.
        this.highPrice = new BigDecimal(hotelSummary.getDouble("highRate")).setScale(2, RoundingMode.HALF_EVEN);
        this.lowPrice = new BigDecimal(hotelSummary.getDouble("lowRate")).setScale(2, RoundingMode.HALF_EVEN);
        this.currencyCode = hotelSummary.optString("rateCurrencyCode");
        this.supplierType = hotelSummary.optString("supplierType");
    }

    /**
     * {@inheritDoc}
     *
     * Simply returns the name field of this object.
     */
    @Override
    public String toString() {
        return this.name;
    }

}
