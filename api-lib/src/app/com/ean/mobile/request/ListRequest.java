/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.HotelInfoList;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelWrangler;

public final class ListRequest extends Request {
    private static final String NUMBER_OF_RESULTS = "10";
    private static final String URL_SUBDIR = "list";
    private static final String CALENDAR_FORMAT = "%tD";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private ListRequest() {
        //see javadoc.
    }

    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * THIS SHOULD NOT BE RUN ON THE MAIN THREAD. It is a long-running network process and so might cause
     * force close dialogs.
     * @param destination The destination to search for hotel availability.
     * @param numberOfAdults The number of adults for the request.
     * @param numberOfChildren The number of children for the request.
     * @param arrivalDate The arrival date of the request.
     * @param departureDate The departure date of the request.
     * @return The list of HotelInfo that were requested by the search parameters.
     * @throws IOException If there was a network-level error.
     * @throws JSONException If the response contained unexpected data.
     * @throws EanWsError If the API encountered an error and was unable to return results.
     */
    public static HotelInfoList searchForHotels(final String destination,
                                                final int numberOfAdults,
                                                final int numberOfChildren,
                                                final Calendar arrivalDate,
                                                final Calendar departureDate)
            throws IOException, JSONException, EanWsError {

        final String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"destinationString", destination},
            {"numberOfResults", NUMBER_OF_RESULTS},
            {"room1", String.format("%d,%d", numberOfAdults, numberOfChildren)},
            {"arrivalDate", String.format("%tD", arrivalDate)},
            {"departureDate", String.format("%tD", departureDate)}
        };

        // TODO: Possibly cache the HotelInfoLists (factory?) such that if the request is performed again
        // within a certain threshold (maybe a day?) the search appears to be instantaneous.
        // This has the potential to be a memory hog given that the HotelImageTuples store the actual
        // bytes of the images they represent, once loaded.
        final JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);


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

        return new HotelInfoList(hotels, cacheKey, cacheLocation, customerSessionId);

    }
}
