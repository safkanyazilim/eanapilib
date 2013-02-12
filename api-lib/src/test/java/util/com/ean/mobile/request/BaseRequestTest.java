/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import org.junit.Before;
import org.junit.Ignore;

import com.ean.mobile.TestConstants;

@Ignore("Contains no actual tests.")
public abstract class BaseRequestTest {

    @Before
    public void setUp() {
        CommonParameters.cid = TestConstants.CID;
        CommonParameters.apiKey = TestConstants.API_KEY;
        CommonParameters.customerUserAgent = TestConstants.CUSTOMER_USER_AGENT;
        CommonParameters.locale = TestConstants.LOCALE.toString();
        CommonParameters.currencyCode = TestConstants.CURRENCY.getCurrencyCode();
        CommonParameters.customerIpAddress = TestConstants.CUSTOMER_IP_ADDRESS;
        CommonParameters.customerSessionId = null;
    }

    protected static StringBuilder buildBaseQueryString() {
        StringBuilder queryString = new StringBuilder();
        queryString.append("cid=");
        queryString.append(CommonParameters.cid);
        queryString.append("&apiKey=");
        queryString.append(CommonParameters.apiKey);
        queryString.append("&customerUserAgent=");
        queryString.append(CommonParameters.customerUserAgent);
        queryString.append("&locale=");
        queryString.append(CommonParameters.locale);
        queryString.append("&currencyCode=");
        queryString.append(CommonParameters.currencyCode);
        queryString.append("&customerIpAddress=");
        queryString.append(CommonParameters.customerIpAddress);
        queryString.append("&customerSessionId=");
        queryString.append(CommonParameters.customerSessionId);
        queryString.append("&minorRev=");
        queryString.append(CommonParameters.minorRev);
        return queryString;
    }

}
