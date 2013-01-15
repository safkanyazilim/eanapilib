/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DestinationTest {

    private static final String TEST_JSON_STRING;

    private static final JSONObject TEST_JSON;

    static {
        InputStream jsonResource = DestinationTest.class.getResourceAsStream("DestinationTest.json");
        if (jsonResource == null) {
            throw new RuntimeException(
                    "Could not load DestinationTest.json for test. Check classpath and resource settings.");
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(jsonResource));
        final StringBuilder jsonBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        } catch (IOException ioe) {
            jsonBuilder.append("error");
        }
        TEST_JSON_STRING = jsonBuilder.toString();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(TEST_JSON_STRING);
        } catch (JSONException jse) {
            jsonObject = null;
        }
        TEST_JSON = jsonObject;
    }

    @Test
    public void testDestinationCreatedCorrectly() throws Exception {
        JSONObject json = TEST_JSON.getJSONObject("well-behaved");

        Destination destination = new Destination(json);

        assertEquals(json.optString("id"), destination.id);
        assertEquals(json.optString("categoryLocalized"), destination.categoryLocalized);
        assertEquals(json.optString("name"), destination.name);
        assertEquals(json.optString("category").toUpperCase(), destination.category.toString());
    }

    @Test
    public void testDestinationNullCategory() throws Exception {
        JSONObject json = TEST_JSON.getJSONObject("null-category");

        Destination destination = new Destination(json);

        assertEquals(json.optString("id"), destination.id);
        assertEquals(json.optString("categoryLocalized"), destination.categoryLocalized);
        assertEquals(json.optString("name"), destination.name);
        assertEquals(Destination.Category.UNKNOWN, destination.category);
    }
}
