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
        assertEquals(Destination.Category.CITY, destination.category);
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

    @Test
    public void testParseCategoryBadResponseName() {
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName(null));
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName(""));
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName("ANDROID-HUT"));
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName("CITY"));
    }

    @Test
    public void testParseCategoryAirportResponse() {
        assertEquals(Destination.Category.AIRPORT, Destination.Category.getByResponseName("AIRPORTS"));
        assertEquals(Destination.Category.AIRPORT, Destination.Category.getByResponseName("airports"));
        assertEquals(Destination.Category.AIRPORT, Destination.Category.getByResponseName("airPORTS"));
    }

    @Test
    public void testParseCategoryCityResponse() {
        assertEquals(Destination.Category.CITY, Destination.Category.getByResponseName("CITIES"));
        assertEquals(Destination.Category.CITY, Destination.Category.getByResponseName("cities"));
        assertEquals(Destination.Category.CITY, Destination.Category.getByResponseName("ciTIEs"));
    }

    @Test
    public void testParseCategoryHotelResponse() {
        assertEquals(Destination.Category.HOTEL, Destination.Category.getByResponseName("HOTELS"));
        assertEquals(Destination.Category.HOTEL, Destination.Category.getByResponseName("hotels"));
        assertEquals(Destination.Category.HOTEL, Destination.Category.getByResponseName("hoTELs"));
    }

    @Test
    public void testParseCategoryLandmarkResponse() {
        assertEquals(Destination.Category.LANDMARK, Destination.Category.getByResponseName("LANDMARKS"));
        assertEquals(Destination.Category.LANDMARK, Destination.Category.getByResponseName("landmarks"));
        assertEquals(Destination.Category.LANDMARK, Destination.Category.getByResponseName("laNDMArks"));
    }

    @Test
    public void testParseCategoryUnknownResponse() {
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName("UNKNOWN"));
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName("unknowns"));
        assertEquals(Destination.Category.UNKNOWN, Destination.Category.getByResponseName("unKNOWns"));
    }
}
