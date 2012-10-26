/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelRoom;
import com.ean.mobile.HotelWrangler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RoomAvailRequest extends Request {
    private static final String URL_SUBDIR = "avail";
    public static HotelWrangler getRoomAvailForHotel(HotelInfo hotel, HotelWrangler wrangler) throws IOException, JSONException {
        String[][] urlPairs = {
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
        JSONObject json = getJsonFromSubdir(URL_SUBDIR, urlPairs);
        // TODO: handler EanWsError objects, such as sold out rooms
        JSONObject err;
        if ((err = json.optJSONObject("EanWsError")) != null) {
            wrangler.getInfos().remove(hotel);
            wrangler.setSelectedInfo(null);
            return wrangler;
        }
        JSONObject resp = json.optJSONObject("HotelRoomAvailabilityResponse");
        if(resp.optJSONArray("HotelRoomResponse") != null) {
            JSONArray hrr = resp.optJSONArray("HotelRoomResponse");
            hotel.hotelRooms = HotelRoom.parseRoomRateDetails(hrr);
            if (hotel.supplierType.equals("S")) {

                RateRulesRequest.getRateRulesForHotel(hotel, wrangler);
            }
        } else if(resp.optJSONObject("HotelRoomResponse") != null) {
            JSONObject hrr = resp.optJSONObject("HotelRoomResponse");
            hotel.hotelRooms = HotelRoom.parseRoomRateDetails(hrr);
            if (hotel.supplierType.equals("S")) {
                RateRulesRequest.getRateRulesForHotel(hotel, wrangler);
            }
        }

        if (hotel.supplierType.equals("S")) {
            if(resp.optJSONArray("HotelRoomResponse") != null) {
                hotel.hotelRooms = HotelRoom.parseRoomRateDetails(resp.optJSONArray("HotelRoomResponse"));
            } else if(resp.optJSONObject("HotelRoomResponse") != null) {
                hotel.hotelRooms = HotelRoom.parseRoomRateDetails(resp.optJSONObject("HotelRoomResponse"));
            }
        }

        hotel.hasRetrievedRoomAvail = true;
        return wrangler;
    }
}
