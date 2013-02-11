/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.List;

import org.junit.Test;

import com.ean.mobile.Destination;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class DestinationRequestIntTest extends BaseRequestTest {

    @Test
    public void testGetDestination() throws Exception {
        DestinationRequest destinationRequest = new DestinationRequest("sea");
        List<Destination> results = RequestProcessor.run(destinationRequest);
        assertThat(results.size(), greaterThan(0));
    }
}
