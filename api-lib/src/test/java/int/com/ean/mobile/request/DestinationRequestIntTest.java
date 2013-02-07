/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.ean.mobile.BaseRequest;
import com.ean.mobile.Destination;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class DestinationRequestIntTest {

    @Before
    public void setUp() {
        BaseRequest.initialize("55505", "cbrzfta369qwyrm9t5b8y8kf", Locale.US.toString(), "USD");
    }

    @Test
    public void testGetDestination() throws Exception {
        DestinationRequest destinationRequest = new DestinationRequest("sea");
        List<Destination> results = RequestProcessor.run(destinationRequest);
        assertThat(results.size(), greaterThan(0));
    }
}
