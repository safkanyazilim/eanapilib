/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.json.JSONObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DestinationTest {

    private static final JSONObject TEST_JSON = JSONFileUtil.loadJsonFromFile("DestinationTest.json");

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
