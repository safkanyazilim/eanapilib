/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
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
