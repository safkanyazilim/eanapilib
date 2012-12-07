/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.exception.EanWsError;

/**
 * The most useful method gets the List of hotels based on the search parameters, particularly the destination passed.
 */
public final class ListRequest extends Request {
    private static final String NUMBER_OF_RESULTS = "10";
    private static final String URL_SUBDIR = "list";

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

        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("cid", CID),
            new BasicNameValuePair("minorRev", MINOR_REV),
            new BasicNameValuePair("apiKey", API_KEY),
            new BasicNameValuePair("locale", LOCALE),
            new BasicNameValuePair("currencyCode", CURRENCY_CODE),
            new BasicNameValuePair("destinationString", destination),
            new BasicNameValuePair("numberOfResults", NUMBER_OF_RESULTS),
            new BasicNameValuePair("room1", formatRoomOccupancy(numberOfAdults, numberOfChildren)),
            new BasicNameValuePair("arrivalDate", formatApiDate(arrivalDate)),
            new BasicNameValuePair("departureDate", formatApiDate(departureDate))
        );

        // TODO: Possibly cache the HotelInfoLists (factory?) such that if the request is performed again
        // within a certain threshold (maybe a day?) the search appears to be instantaneous.
        // This has the potential to be a memory hog given that the HotelImageTuples store the actual
        // bytes of the images they represent, once loaded.
        // TODO: Support pagination via cachekey and so forth
        final JSONObject json = performApiRequest(URL_SUBDIR, urlParameters);

        final JSONObject listResponse = json.getJSONObject("HotelListResponse");

        if (listResponse.has("EanWsError")) {
            throw EanWsError.fromJson(listResponse.getJSONObject("EanWsError"));
        }

        final String cacheKey = listResponse.optString("cacheKey");
        final String cacheLocation = listResponse.optString("cacheLocation");
        final String customerSessionId = listResponse.optString("customerSessionId");
        final JSONArray hotelList = listResponse
            .getJSONObject("HotelList")
            .getJSONArray("HotelSummary");
        final List<HotelInfo> hotels = new ArrayList<HotelInfo>(hotelList.length());
        for (int i = 0; i < hotelList.length(); i++) {
            hotels.add(new HotelInfo(hotelList.getJSONObject(i)));
        }

        return new HotelInfoList(hotels, cacheKey, cacheLocation, customerSessionId);

    }
}
