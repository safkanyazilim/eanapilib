/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.List;

import org.junit.Test;

import com.ean.mobile.Destination;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class DestinationRequestIntTest extends RequestTestBase {

    @Test
    public void testGetDestination() throws Exception {
        DestinationRequest destinationRequest = new DestinationRequest("sea");
        List<Destination> results = RequestProcessor.run(destinationRequest);
        assertThat(results.size(), greaterThan(0));
    }

    @Test
    public void testGetDestinationNoResults() throws Exception {
        DestinationRequest destinationRequest = new DestinationRequest("");
        List<Destination> results = RequestProcessor.run(destinationRequest);
        assertThat(results.size(), equalTo(0));
    }

    @Test
    public void testGetDestinationInvalidDestination() throws Exception {
        DestinationRequest destinationRequest = new DestinationRequest("Death Star");
        List<Destination> results = RequestProcessor.run(destinationRequest);
        assertThat(results.size(), equalTo(0));
    }
}
