/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

import com.ean.mobile.LatLongAddress;

/**
 * The holder for information about a particular hotel.
 */
public final class Hotel {

    /**
     * The name of this hotel.
     */
    public final String name;

    /**
     * The short description of this hotel.
     */
    public final String shortDescription;

    /**
     * The location description of this hotel.
     */
    public final String locationDescription;

    /**
     * The star rating of this hotel.
     */
    public final BigDecimal starRating;

    /**
     * The main HotelImageTuple for this hotel.
     */
    public final HotelImageTuple mainHotelImageTuple;

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
    public final LatLongAddress address;

    /**
     * The type of supplier for the hotel.
     */
    public final SupplierType supplierType;

    /**
     * The high price of the hotel, in the currency specified by {currencyCode}.
     */
    public final BigDecimal highPrice;

    /**
     * The low price found for the hotel, in the currency specified by {currencyCode}.
     */
    public final BigDecimal lowPrice;

    /**
     * The constructor that constructs the hotel info from a JSONObject.
     * @param hotelSummary The object holding the hotel's info.
     * @throws JSONException If there is a problem with the JSON objects
     * @throws MalformedURLException If the thumbnail url is not correctly formatted.
     */
    public Hotel(final JSONObject hotelSummary) throws JSONException, MalformedURLException {
        this.name = Html.fromHtml(hotelSummary.optString("name")).toString();
        this.hotelId = hotelSummary.optLong("hotelId");
        this.address = new LatLongAddress(hotelSummary);
        this.shortDescription =  Html.fromHtml(hotelSummary.optString("shortDescription")).toString();
        this.locationDescription = Html.fromHtml(hotelSummary.optString("locationDescription")).toString();
        this.starRating = parseStarRating(hotelSummary.optString("hotelRating"));
        final String thumbnailString = hotelSummary.optString("thumbNailUrl").replace("_t.jpg", "_n.jpg");
        this.mainHotelImageTuple = new HotelImageTuple(thumbnailString, null, null);
        this.highPrice = new BigDecimal(hotelSummary.getDouble("highRate")).setScale(2, RoundingMode.HALF_EVEN);
        this.lowPrice = new BigDecimal(hotelSummary.getDouble("lowRate")).setScale(2, RoundingMode.HALF_EVEN);
        this.currencyCode = hotelSummary.optString("rateCurrencyCode");
        this.supplierType = SupplierType.getByCode(hotelSummary.optString("supplierType"));
    }

    /**
     * Gets the star rating (a BigDecimal) from the string representation of the star rating.
     * @param starRating The String representation of a star rating.
     * @return The BigDecimal representation of a star rating.
     */
    public static BigDecimal parseStarRating(final String starRating) {
        return starRating == null || "".equals(starRating) ? BigDecimal.ZERO : new BigDecimal(starRating).setScale(1);
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
