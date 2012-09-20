package com.ean.mobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HotelRoom {

    public String
        description,
        promoDescription,
        rateCode,
        roomTypeCode;

    public RateInfo rate;

    public HotelRoom(JSONObject roomRateDetail) throws JSONException {

        this.description = roomRateDetail.optString("roomTypeDescription", "");
        this.rateCode = roomRateDetail.optString("rateCode", "");
        this.roomTypeCode = roomRateDetail.optString("roomTypeCode");
        this.promoDescription = roomRateDetail.optString("promoDescription");
        if (roomRateDetail.optJSONArray("RateInfos") != null) {
            this.rate = RateInfo.parseRateInfos(roomRateDetail.getJSONArray("RateInfos")).get(0);
        } else if (roomRateDetail.optJSONObject("RateInfos") != null) {
            this.rate = RateInfo.parseRateInfos(roomRateDetail.getJSONObject("RateInfos")).get(0);
        } else {
            // then this was a sabre response that requires ANOTHER call to get the rate information
            // but that is handled by the RoomAvail request, so we do nothing with the rates.
        }
    }


    public static ArrayList<HotelRoom> parseRoomRateDetails(JSONArray hotelRoomResponseJson) throws JSONException{
        ArrayList<HotelRoom> hotelRooms = new ArrayList<HotelRoom>();
      //  Log.d(EANMobileConstants.DEBUG_TAG, "parsing room rate details");
        for(int j=0; j < hotelRoomResponseJson.length(); j++) {
            hotelRooms.add(new HotelRoom(hotelRoomResponseJson.getJSONObject(j)));
        }
      //  Log.d(EANMobileConstants.DEBUG_TAG, "done parsing room rate details");

        return hotelRooms;
    }

    public static ArrayList<HotelRoom> parseRoomRateDetails(JSONObject hotelRoomResponseJson) throws JSONException{
        ArrayList<HotelRoom> hotelRooms = new ArrayList<HotelRoom>();
        //Log.d(EANMobileConstants.DEBUG_TAG, "parsing single room rate details");
        hotelRooms.add(new HotelRoom(hotelRoomResponseJson));
       // Log.d(EANMobileConstants.DEBUG_TAG, "done parsing single room rate details");

        return hotelRooms;
    }


    public BigDecimal getTotalBaseRate() {
        return rate.getBaseRateTotal();
    }

    public BigDecimal getTotalRate() {
        return rate.getRateTotal();
    }

    public BigDecimal getTaxesAndFees() {
        BigDecimal tanf = BigDecimal.ZERO;

        return tanf;
    }
}
