/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HotelInfoTest {

    @Test
    public void testCompareTo() throws MalformedURLException, JSONException {
        JSONObject stubJsonObject = createJsonObject();
        HotelInfo hotelInfoZero = new HotelInfo(stubJsonObject, 0);
        HotelInfo hotelInfoOne = new HotelInfo(stubJsonObject, 1);

        assertEquals(-1, hotelInfoZero.compareTo(hotelInfoOne));
        assertEquals(1, hotelInfoOne.compareTo(hotelInfoZero));
        assertEquals(0, hotelInfoOne.compareTo(hotelInfoOne));
        assertEquals(0, hotelInfoZero.compareTo(hotelInfoZero));
    }

    @Test
    public void testToString() throws JSONException, MalformedURLException {
        JSONObject stubJsonObject = createJsonObject();
        HotelInfo hotelInfo = new HotelInfo(stubJsonObject, 0);
        assertEquals("0 The Benjamin", hotelInfo.toString());
    }

    private JSONObject createJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "The Benjamin");
        jsonObject.put("hotelID", 7);

        jsonObject.put("highRate", 4.5d);
        jsonObject.put("lowRate", 3.5d);

        return jsonObject;
    }
}