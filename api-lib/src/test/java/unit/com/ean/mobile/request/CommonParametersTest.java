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

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.ean.mobile.TestConstants;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.ean.mobile.Constants;

public class CommonParametersTest {

    private static final int SIGNATURE_SLEEP_TIME = 1100;
    private static final String MD5_HASH_REGEX = "^[a-f0-9]{32}$";

    @Before
    public void setUp() {
        CommonParameters.cid = null;
        CommonParameters.apiKey = null;
        CommonParameters.customerUserAgent = null;
        CommonParameters.locale = null;
        CommonParameters.currencyCode = null;
        CommonParameters.customerIpAddress = null;
        CommonParameters.customerSessionId = null;
    }

    @Test(expected = RuntimeException.class)
    public void testAsNameValuePairsInvalidNoneSet() {
        CommonParameters.asNameValuePairs();
    }

    @Test(expected = RuntimeException.class)
    public void testAsNameValuePairsInvalidOptionalSet() {
        CommonParameters.customerUserAgent = TestConstants.CUSTOMER_USER_AGENT;
        CommonParameters.locale = TestConstants.LOCALE.toString();
        CommonParameters.currencyCode = TestConstants.CURRENCY.getCurrencyCode();
        CommonParameters.customerIpAddress = TestConstants.CUSTOMER_IP_ADDRESS;
        CommonParameters.customerSessionId = TestConstants.CUSTOMER_SESSION_ID;

        CommonParameters.asNameValuePairs();
    }

    @Test
    public void testAsNameValuePairsValidRequiredSet() {
        CommonParameters.cid = TestConstants.CID;
        CommonParameters.apiKey = TestConstants.API_KEY;
        CommonParameters.signatureSecret = null;

        List<NameValuePair> nameValuePairs = CommonParameters.asNameValuePairs();
        assertNotNull(nameValuePairs);
        assertEquals(3, nameValuePairs.size());
        assertThat(nameValuePairs, Matchers.<NameValuePair>contains(
            new BasicNameValuePair("cid", TestConstants.CID),
            new BasicNameValuePair("apiKey", TestConstants.API_KEY),
            new BasicNameValuePair("minorRev", Constants.MINOR_REV)
        ));
    }

    @Test
    public void testAsNameValuePairsValidAllSet() {
        CommonParameters.cid = TestConstants.CID;
        CommonParameters.apiKey = TestConstants.API_KEY;
        CommonParameters.customerUserAgent = TestConstants.CUSTOMER_USER_AGENT;
        CommonParameters.locale = TestConstants.LOCALE.toString();
        CommonParameters.currencyCode = TestConstants.CURRENCY.getCurrencyCode();
        CommonParameters.customerIpAddress = TestConstants.CUSTOMER_IP_ADDRESS;
        CommonParameters.customerSessionId = TestConstants.CUSTOMER_SESSION_ID;
        CommonParameters.signatureSecret = null;

        List<NameValuePair> nameValuePairs = CommonParameters.asNameValuePairs();
        assertNotNull(nameValuePairs);
        assertEquals(8, nameValuePairs.size());
        assertThat(nameValuePairs, Matchers.<NameValuePair>contains(
            new BasicNameValuePair("cid", TestConstants.CID),
            new BasicNameValuePair("apiKey", TestConstants.API_KEY),
            new BasicNameValuePair("customerUserAgent", TestConstants.CUSTOMER_USER_AGENT),
            new BasicNameValuePair("locale", TestConstants.LOCALE.toString()),
            new BasicNameValuePair("currencyCode", TestConstants.CURRENCY.toString()),
            new BasicNameValuePair("customerIpAddress", TestConstants.CUSTOMER_IP_ADDRESS),
            new BasicNameValuePair("customerSessionId", TestConstants.CUSTOMER_SESSION_ID),
            new BasicNameValuePair("minorRev", Constants.MINOR_REV)
        ));
    }

    @Test
    public void testAsNameValuePairsValidAllSetWithSignature() {
        CommonParameters.cid = TestConstants.CID;
        CommonParameters.apiKey = TestConstants.API_KEY;
        CommonParameters.customerUserAgent = TestConstants.CUSTOMER_USER_AGENT;
        CommonParameters.locale = TestConstants.LOCALE.toString();
        CommonParameters.currencyCode = TestConstants.CURRENCY.getCurrencyCode();
        CommonParameters.customerIpAddress = TestConstants.CUSTOMER_IP_ADDRESS;
        CommonParameters.customerSessionId = TestConstants.CUSTOMER_SESSION_ID;
        CommonParameters.signatureSecret = TestConstants.SHARED_SECRET;

        List<NameValuePair> nameValuePairs = CommonParameters.asNameValuePairs();
        assertNotNull(nameValuePairs);
        assertEquals(9, nameValuePairs.size());
        assertThat(nameValuePairs, Matchers.<NameValuePair>hasItems(
                new BasicNameValuePair("cid", TestConstants.CID),
                new BasicNameValuePair("apiKey", TestConstants.API_KEY),
                new BasicNameValuePair("customerUserAgent", TestConstants.CUSTOMER_USER_AGENT),
                new BasicNameValuePair("locale", TestConstants.LOCALE.toString()),
                new BasicNameValuePair("currencyCode", TestConstants.CURRENCY.toString()),
                new BasicNameValuePair("customerIpAddress", TestConstants.CUSTOMER_IP_ADDRESS),
                new BasicNameValuePair("customerSessionId", TestConstants.CUSTOMER_SESSION_ID),
                new BasicNameValuePair("minorRev", Constants.MINOR_REV)
        ));

        boolean found = false;
        for (NameValuePair pair : nameValuePairs) {
            if ("sig".equals(pair.getName())) {
                assertMatchesRegexPattern(pair.getValue());
                found = true;
                break;
            }
        }

        assertTrue("sig was not found in the nameValuePairs", found);
    }

    @Test
    public void testGetSignatureWithNull() throws Exception {
        CommonParameters.signatureSecret = null;
        assertNull(Whitebox.invokeMethod(CommonParameters.class, "getSignature"));
    }

    @Test
    public void testGetSignatureWithRealValueHashLooksCorrect() throws Exception {
        CommonParameters.signatureSecret = TestConstants.SHARED_SECRET;
        String signature = Whitebox.invokeMethod(CommonParameters.class, "getSignature");

        assertNotNull(signature);
        assertMatchesRegexPattern(signature);
    }

    @Test
    public void testGetSignatureThatValuesChangeWithTime() throws Exception {
        CommonParameters.signatureSecret = TestConstants.SHARED_SECRET;
        String signature = Whitebox.invokeMethod(CommonParameters.class, "getSignature");

        //Sleep for a little over a second to make sure we get a different signature.
        Thread.sleep(SIGNATURE_SLEEP_TIME);
        assertThat(signature, not(equalTo(Whitebox.invokeMethod(CommonParameters.class, "getSignature"))));
    }

    private void assertMatchesRegexPattern(final String mdFiveString) {
        assertTrue(
                "Signature doesn't match the expected MD5 format, instead got: " + mdFiveString,
                mdFiveString.matches(MD5_HASH_REGEX));
    }

}
