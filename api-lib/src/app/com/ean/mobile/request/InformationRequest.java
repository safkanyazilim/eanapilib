/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;
import com.ean.mobile.EANMobileConstants;
import com.ean.mobile.HotelImageTuple;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelWrangler;

public final class InformationRequest extends Request {
    private static final String URL_SUBDIR = "info";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private InformationRequest() {
        //see javadoc.
    }


    public static HotelWrangler getHotelInformation(final HotelInfo hotel, final HotelWrangler wrangler)
            throws IOException, JSONException {
        final String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"customerSessionId", wrangler.getCustomerSessionId()},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"hotelId", hotel.hotelId},
            {"options", "HOTEL_DETAILS,HOTEL_IMAGES"}
        };
        final JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);

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
            Log.d(EANMobileConstants.DEBUG_TAG, "Found " + hotel.images.size() + " images");
        }

        hotel.hasRetrievedHotelInfo = true;
        return wrangler;
    }
}
