/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;
import com.ean.mobile.Constants;
import com.ean.mobile.HotelImageTuple;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.exception.EanWsError;

/**
 * Uses getHotelInformation to get the rest of the hotel's information, including images
 * and the hotel's full description.
 */
public final class InformationRequest extends Request {
    private static final String URL_SUBDIR = "info";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private InformationRequest() {
        //see javadoc.
    }

    /**
     * Gets the rest of the information about a hotel not included in previous calls.
     * @param hotel The hotel for which to gather more information.
     * @param customerSessionId The session of the customer so the search can happen potentially more quickly.
     * @throws IOException If there is an error communicating on the network.
     * @throws JSONException If the json returned is malformed somehow
     * @throws EanWsError If there was an error returned by the api, often caused by bad request data.
     */
    public static void getHotelInformation(final HotelInfo hotel, final String customerSessionId)
            throws IOException, JSONException, EanWsError {
        final List<NameValuePair> urlParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("cid", CID),
            new BasicNameValuePair("minorRev", MINOR_REV),
            new BasicNameValuePair("apiKey", API_KEY),
            new BasicNameValuePair("customerSessionId", customerSessionId),
            new BasicNameValuePair("locale", LOCALE),
            new BasicNameValuePair("currencyCode", CURRENCY_CODE),
            new BasicNameValuePair("hotelId", Long.toString(hotel.hotelId)),
            new BasicNameValuePair("options", "HOTEL_DETAILS,HOTEL_IMAGES")
        );
        final JSONObject json = performApiRequest(URL_SUBDIR, urlParameters);

        if (json != null) {
            final JSONObject infoResp = json.getJSONObject("HotelInformationResponse");
            final JSONObject details = infoResp.getJSONObject("HotelDetails");
            final JSONArray images = infoResp.getJSONObject("HotelImages")
                .getJSONArray("HotelImage");

            hotel.longDescription = Html.fromHtml(details.optString("propertyDescription")).toString();

            hotel.images = new ArrayList<HotelImageTuple>();

            JSONObject image;
            for (int i = 0; i < images.length(); i++) {
                image = images.getJSONObject(i);
                hotel.images.add(
                    new HotelImageTuple(
                        new URL(image.optString("thumbnailUrl")),
                        new URL(image.optString("url")),
                        image.optString("caption")));
            }
            Log.d(Constants.DEBUG_TAG, "Found " + hotel.images.size() + " images");
        }

        hotel.hasRetrievedHotelInfo = true;
    }
}
