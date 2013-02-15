/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.TestConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.ean.mobile.Constants;

public class CommonParametersTest {

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
}
