/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.Cancellation;
import com.ean.mobile.JSONFileUtil;
import com.ean.mobile.exception.EanWsError;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class CancellationRequestTest {

    private CancellationRequest cancellationRequest;

    @Before
    public void setUp() {
        cancellationRequest = new CancellationRequest(1234L, 5678L, "test@expedia.com", "test");
    }

    @Test
    public void testConsumeNullJson() throws Exception {
        assertNull(cancellationRequest.consume(null));
    }

    @Test(expected = JSONException.class)
    public void testConsumeEmptyJson() throws Exception {
        cancellationRequest.consume(new JSONObject());
    }

    @Test(expected = JSONException.class)
    public void testConsumeInvalidJson() throws Exception {
        cancellationRequest.consume(JSONFileUtil.loadJsonFromFile("invalid-cancellation.json"));
    }

    @Test(expected = EanWsError.class)
    public void testConsumeEanWsError() throws Exception {
        cancellationRequest.consume(JSONFileUtil.loadJsonFromFile("error-cancellation.json"));
    }

    @Test
    public void testConsume() throws Exception {
        Cancellation cancellation
            = cancellationRequest.consume(JSONFileUtil.loadJsonFromFile("valid-cancellation.json"));
        assertNotNull(cancellation);
        assertEquals("12345678", cancellation.cancellationNumber);
    }

    @Test
    public void testGetUri() throws Exception {
        StringBuilder queryString = new StringBuilder(155);
        queryString.append("cid=55505&apiKey=cbrzfta369qwyrm9t5b8y8kf&minorRev=20&customerUserAgent=Android");
        queryString.append("&itineraryId=1234&confirmationNumber=5678&email=test@expedia.com&reason=test");

        final URI uri = cancellationRequest.getUri();
        assertEquals("http", uri.getScheme());
        assertEquals("api.ean.com", uri.getHost());
        assertEquals("/ean-services/rs/hotel/v3/cancel", uri.getPath());
        assertEquals(queryString.toString(), uri.getQuery());
    }

    @Test
    public void testIsSecure() {
        assertThat(cancellationRequest.requiresSecure(), is(false));
    }

}
