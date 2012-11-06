/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.EanWsError;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelWrangler;

public final class ListRequest extends Request {
    private static final String NUMBER_OF_RESULTS = "10";
    private static final String URL_SUBDIR = "list";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private ListRequest() {
        //see javadoc.
    }


    public static HotelWrangler searchForHotels(final String destination, final HotelWrangler wrangler)
            throws IOException, JSONException, EanWsError {

        final String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"destinationString", destination},
            {"numberOfResults", NUMBER_OF_RESULTS},
            {"room1", wrangler.getNumberOfAdults().toString() + "," + wrangler.getNumberOfChildren().toString()},
            {"arrivalDate", formatDate(wrangler.getArrivalDate())},
            {"departureDate", formatDate(wrangler.getDepartureDate())}
        };
        final JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);

        if (json != null) {
            final JSONObject listResp = json.getJSONObject("HotelListResponse");

            if (listResp.has("EanWsError")) {
                throw EanWsError.fromJson(listResp.getJSONObject("EanWsError"));
            }


            final String cacheKey = listResp.optString("cacheKey");
            final String cacheLocation = listResp.optString("cacheLocation");
            final String customerSessionId = listResp.optString("customerSessionId");
            final JSONArray hotelList = listResp
                .getJSONObject("HotelList")
                .getJSONArray("HotelSummary");
            final List<HotelInfo> hotels = new ArrayList<HotelInfo>(hotelList.length());
            for (int i = 0; i < hotelList.length(); i++) {
                hotels.add(new HotelInfo(hotelList.getJSONObject(i)));
            }

            wrangler.setInfos(hotels);
            wrangler.setCacheLocation(cacheLocation);
            wrangler.setCustomerSessionId(customerSessionId);
            wrangler.setCacheKey(cacheKey);
        }

        return wrangler;

    }

    private static String formatDate(final Date date) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date);
    }
}
