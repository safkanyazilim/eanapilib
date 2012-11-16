/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.HotelWrangler;

public final class RateRulesRequest extends Request {
    private static final String URL_SUBDIR = "rules";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private RateRulesRequest() {
        //see javadoc.
    }

    public static HotelWrangler getRateRulesForHotel(final HotelInfo hotel, final HotelWrangler wrangler)
            throws IOException, JSONException {
        for (HotelRoom room : hotel.hotelRooms) {
            getRateRulesForRoom(hotel, wrangler, room);
        }
        return wrangler;
    }

    public static HotelWrangler getRateRulesForRoom(final HotelInfo hotel,
                                                    final HotelWrangler wrangler,
                                                    final HotelRoom room)
            throws IOException, JSONException {
        final String room1Occupancy
            = wrangler.getNumberOfAdults().toString() + "," + wrangler.getNumberOfChildren().toString();
        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("cid", CID),
            new BasicNameValuePair("minorRev", MINOR_REV),
            new BasicNameValuePair("apiKey", API_KEY),
            new BasicNameValuePair("locale", LOCALE),
            new BasicNameValuePair("currencyCode", CURRENCY_CODE),
            new BasicNameValuePair("arrivalDate", formatApiDate(wrangler.getArrivalDate())),
            new BasicNameValuePair("departureDate", formatApiDate(wrangler.getDepartureDate())),
            new BasicNameValuePair("customerSessionId", wrangler.getCustomerSessionId()),
            new BasicNameValuePair("hotelId", hotel.hotelId),
            new BasicNameValuePair("supplierType", hotel.supplierType),
            new BasicNameValuePair("rateCode", room.rateCode),
            new BasicNameValuePair("roomTypeCode", room.roomTypeCode),
            new BasicNameValuePair("room1", room1Occupancy)
        );
        final JSONObject json = performApiRequest(URL_SUBDIR, urlParameters);

        return wrangler;
    }
}
