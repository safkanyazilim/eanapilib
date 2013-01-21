/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.net.URI;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.Destination;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


public class DestinationLookupTest {

    DestinationLookup destinationLookup;

    @Before
    public void setUp() {
        destinationLookup = new DestinationLookup("sea");
    }

    @Test(expected = JSONException.class)
    public void testConsumeEmptyJson() throws Exception {
        destinationLookup.consume(new JSONObject());
    }

    @Test
    public void testConsumeValidJson() throws Exception {
        StringBuilder jsonString = new StringBuilder(211);
        jsonString.append("{'items':[{'categoryLocalized':'Cities/Areas',)");
        jsonString.append("'id':'AREA-4ff23c22-e4ff-4834-91bd-08bad605fe8c|cities|Sea World ");
        jsonString.append("Orlando, FL, United States)','category':'cities',");
        jsonString.append("'name':'Sea World (Orlando, FL, United States)'}]}");

        List<Destination> destinations = destinationLookup.consume(new JSONObject(jsonString.toString()));

        assertNotNull(destinations);
        assertThat(destinations, is(not(empty())));
    }

    @Test
    public void testGetUriWithQueryString() throws Exception {
        final URI uri = destinationLookup.getUri();
        doUriAssertions(uri);
        assertEquals("propertyName=sea", uri.getQuery());
    }

    @Test
    public void testGetUriNoQueryString() throws Exception {
        destinationLookup = new DestinationLookup(null);
        final URI uri = destinationLookup.getUri();
        doUriAssertions(uri);
        assertNull(uri.getQuery());
    }

    @Test
    public void testIsSecure() {
        assertFalse(new DestinationLookup(null).isSecure());
    }
    
    private static void doUriAssertions(final URI uri) {
        assertEquals("http", uri.getScheme());
        assertEquals("www.travelnow.com", uri.getHost());
        assertEquals("/templates/349176/destination", uri.getPath());
    }

}