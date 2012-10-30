/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.HotelWrangler;

public final class RoomAvailRequest extends Request {
    private static final String URL_SUBDIR = "avail";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private RoomAvailRequest() {
        //see javadoc.
    }


    public static HotelWrangler getRoomAvailForHotel(final HotelInfo hotel, final HotelWrangler wrangler)
            throws IOException, JSONException {
        final String[][] urlPairs = {
            {"cid", CID},
            {"minorRev", MINOR_REV},
            {"apiKey", API_KEY},
            {"locale", LOCALE},
            {"currencyCode", CURRENCY_CODE},
            {"arrivalDate", DATE_FORMAT.format(wrangler.getArrivalDate())},
            {"departureDate", DATE_FORMAT.format(wrangler.getDepartureDate())},
            {"includeDetails", "true"},
            {"customerSessionId", wrangler.getCustomerSessionId()},
            {"hotelId", hotel.hotelId},
            {"room1", wrangler.getNumberOfAdults().toString() + "," + wrangler.getNumberOfChildren().toString()}
        };
        final JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);
        // TODO: handler EanWsError objects, such as sold out rooms
        JSONObject err;
        if ((err = json.optJSONObject("EanWsError")) != null) {
            wrangler.getInfos().remove(hotel);
            wrangler.setSelectedInfo(null);
            return wrangler;
        }
        final JSONObject resp = json.optJSONObject("HotelRoomAvailabilityResponse");
        if (resp.optJSONArray("HotelRoomResponse") != null) {
            JSONArray hrr = resp.optJSONArray("HotelRoomResponse");
            hotel.hotelRooms = HotelRoom.parseRoomRateDetails(hrr);
            if (hotel.supplierType.equals("S")) {

                RateRulesRequest.getRateRulesForHotel(hotel, wrangler);
            }
        } else if (resp.optJSONObject("HotelRoomResponse") != null) {
            final JSONObject hotelRoomResponse = resp.optJSONObject("HotelRoomResponse");
            hotel.hotelRooms = HotelRoom.parseRoomRateDetails(hotelRoomResponse);
            if (hotel.supplierType.equals("S")) {
                RateRulesRequest.getRateRulesForHotel(hotel, wrangler);
            }
        }

        if (hotel.supplierType.equals("S")) {
            if (resp.optJSONArray("HotelRoomResponse") != null) {
                hotel.hotelRooms = HotelRoom.parseRoomRateDetails(resp.optJSONArray("HotelRoomResponse"));
            } else if (resp.optJSONObject("HotelRoomResponse") != null) {
                hotel.hotelRooms = HotelRoom.parseRoomRateDetails(resp.optJSONObject("HotelRoomResponse"));
            }
        }

        hotel.hasRetrievedRoomAvail = true;
        return wrangler;
    }
}
