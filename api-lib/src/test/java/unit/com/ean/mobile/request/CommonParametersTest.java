/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.List;

import org.apache.http.NameValuePair;
import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.TestConstants;

import static org.hamcrest.Matchers.equalTo;
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
        assertThat(nameValuePairs.size(), equalTo(3));
        assertEquals(nameValuePairs.get(0).toString(), String.format("cid=%s", TestConstants.CID));
        assertEquals(nameValuePairs.get(1).toString(), String.format("apiKey=%s", TestConstants.API_KEY));
        assertEquals(nameValuePairs.get(2).toString(), String.format("minorRev=%s", Constants.MINOR_REV));
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
        assertThat(nameValuePairs.size(), equalTo(8));
        assertEquals(
            nameValuePairs.get(0).toString(), String.format("cid=%s", TestConstants.CID));
        assertEquals(
            nameValuePairs.get(1).toString(), String.format("apiKey=%s", TestConstants.API_KEY));
        assertEquals(
            nameValuePairs.get(2).toString(), String.format("customerUserAgent=%s", TestConstants.CUSTOMER_USER_AGENT));
        assertEquals(
            nameValuePairs.get(3).toString(), String.format("locale=%s", TestConstants.LOCALE));
        assertEquals(
            nameValuePairs.get(4).toString(), String.format("currencyCode=%s", TestConstants.CURRENCY));
        assertEquals(
            nameValuePairs.get(5).toString(), String.format("customerIpAddress=%s", TestConstants.CUSTOMER_IP_ADDRESS));
        assertEquals(
            nameValuePairs.get(6).toString(), String.format("customerSessionId=%s", TestConstants.CUSTOMER_SESSION_ID));
        assertEquals(
            nameValuePairs.get(7).toString(), String.format("minorRev=%s", Constants.MINOR_REV));
    }
}
