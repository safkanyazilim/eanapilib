package com.ean.mobile.request;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelWrangler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRequest extends Request {
    private static final String NUMBER_OF_RESULTS = "10";
    private static final String URL_SUBDIR = "list";

    public static HotelWrangler searchForHotels(String destination, HotelWrangler wrangler) throws IOException, JSONException {

        String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"destinationString", destination},
            {"numberOfResults", NUMBER_OF_RESULTS},
            {"room1", wrangler.getNumberOfAdults().toString() + "," + wrangler.getNumberOfChildren().toString()},
            {"options", "HOTEL_SUMMARY"}
        };
        JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);

        List<HotelInfo> hotels = new ArrayList<HotelInfo>();
        String cacheKey = "",
               cacheLocation = "",
               customerSessionId = "";
        if (json != null){
            JSONObject listResp = json.getJSONObject("HotelListResponse");
            wrangler.setCacheKey(listResp.optString("cacheKey"))
                    .setCacheLocation(cacheLocation)
                    .setCustomerSessionId(customerSessionId);
            cacheLocation = listResp.optString("cacheLocation");
            customerSessionId = listResp.optString("customerSessionId");
            JSONArray hotelList = listResp
                                    .getJSONObject("HotelList")
                                    .getJSONArray("HotelSummary");
            //Log.d(EANMobileConstants.DEBUG_TAG, "number in array" + hotelList.length()+"");
            //Log.d(EANMobileConstants.DEBUG_TAG, "hotellist size" + json.getJSONObject("HotelListResponse")
            //                                     .getJSONObject("HotelList")
            //                                     .getString("@size"));
            for (int i=0; i < hotelList.length(); i++) {
                hotels.add(new HotelInfo(hotelList.getJSONObject(i)));
            }
            wrangler.setInfos(hotels);
        }
        
        return wrangler;

    }
}
