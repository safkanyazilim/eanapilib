/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;


import android.text.Html;
import android.util.Log;
import com.ean.mobile.EANMobileConstants;
import com.ean.mobile.HotelImageTuple;
import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelWrangler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class InformationRequest extends Request {
    private static final String URL_SUBDIR = "info";

    public static HotelWrangler getHotelInformation(HotelInfo hotel, HotelWrangler wrangler) throws IOException, JSONException {
        String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"customerSessionId", wrangler.getCustomerSessionId()},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"hotelId", hotel.hotelId},
            {"options", "HOTEL_DETAILS,HOTEL_IMAGES"}
        };
        JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);

        if(json != null) {
            JSONObject infoResp = json.getJSONObject("HotelInformationResponse");
            JSONObject details = infoResp.getJSONObject("HotelDetails");
            JSONArray images = infoResp.getJSONObject("HotelImages")
                                       .getJSONArray("HotelImage");


            hotel.longDescription = Html.fromHtml(details.optString("propertyDescription")).toString();

            hotel.images = new ArrayList<HotelImageTuple>();

            for (int i=0; i < images.length(); i++) {
                JSONObject image = images.getJSONObject(i);
                hotel.images.add(
                    new HotelImageTuple(image.optString("thumbnailUrl"),
                                        image.optString("url"),
                                        image.optString("caption")));
            }
            Log.d(EANMobileConstants.DEBUG_TAG, "Found " + hotel.images.size() + " images");
        }

        hotel.hasRetrievedHotelInfo = true;
        return wrangler;
    }
}
