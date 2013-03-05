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

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
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

public class DestinationRequestTest {

    DestinationRequest destinationRequest;

    @Before
    public void setUp() {
        destinationRequest = new DestinationRequest("sea");
    }

    @Test(expected = JSONException.class)
    public void testConsumeEmptyJson() throws Exception {
        destinationRequest.consume(new JSONObject());
    }

    @Test
    public void testConsumeValidJson() throws Exception {
        StringBuilder jsonString = new StringBuilder(211);
        jsonString.append("{'items':[{'categoryLocalized':'Cities/Areas',)");
        jsonString.append("'id':'AREA-4ff23c22-e4ff-4834-91bd-08bad605fe8c|cities|Sea World ");
        jsonString.append("Orlando, FL, United States)','category':'cities',");
        jsonString.append("'name':'Sea World (Orlando, FL, United States)'}]}");

        List<Destination> destinations = destinationRequest.consume(new JSONObject(jsonString.toString()));

        assertNotNull(destinations);
        assertThat(destinations, is(not(empty())));
    }

    @Test
    public void testGetUriWithQueryString() throws Exception {
        final URI uri = destinationRequest.getUri();
        doUriAssertions(uri);
        assertEquals("propertyName=sea", uri.getQuery());
    }

    @Test
    public void testGetUriNoQueryString() throws Exception {
        destinationRequest = new DestinationRequest(null);
        final URI uri = destinationRequest.getUri();
        doUriAssertions(uri);
        assertNull(uri.getQuery());
    }

    @Test
    public void testIsSecure() {
        assertFalse(new DestinationRequest(null).requiresSecure());
    }
    
    private static void doUriAssertions(final URI uri) {
        assertEquals("http", uri.getScheme());
        assertEquals("www.travelnow.com", uri.getHost());
        assertEquals("/templates/349176/destination", uri.getPath());
    }

}