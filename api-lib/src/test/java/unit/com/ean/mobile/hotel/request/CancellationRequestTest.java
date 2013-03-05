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

package com.ean.mobile.hotel.request;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.JSONFileUtil;
import com.ean.mobile.TestConstants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.Cancellation;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.RequestTestBase;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class CancellationRequestTest extends RequestTestBase {

    private CancellationRequest cancellationRequest;

    @Before
    public void setUp() {
        super.setUp();
        CommonParameters.customerSessionId = TestConstants.CUSTOMER_SESSION_ID;
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
        StringBuilder queryString = buildBaseQueryString();
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
