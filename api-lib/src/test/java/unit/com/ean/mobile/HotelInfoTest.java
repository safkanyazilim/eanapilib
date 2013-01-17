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
    public void testToString() throws JSONException, MalformedURLException {
        JSONObject stubJsonObject = createJsonObject();
        HotelInfo hotelInfo = new HotelInfo(stubJsonObject);
        assertEquals("The Benjamin", hotelInfo.toString());
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
