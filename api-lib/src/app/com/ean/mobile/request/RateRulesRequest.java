/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;

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
        String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"arrivalDate", DATE_FORMAT.format(wrangler.getArrivalDate())},
            {"departureDate", DATE_FORMAT.format(wrangler.getDepartureDate())},
            {"customerSessionId", wrangler.getCustomerSessionId()},
            {"hotelId", hotel.hotelId},
            {"supplierType", hotel.supplierType},
            {"rateCode", room.rateCode},
            {"roomTypeCode", room.roomTypeCode},
            {"room1", wrangler.getNumberOfAdults().toString() + "," + wrangler.getNumberOfChildren().toString()},
        };
        JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);

        return wrangler;
    }
}
