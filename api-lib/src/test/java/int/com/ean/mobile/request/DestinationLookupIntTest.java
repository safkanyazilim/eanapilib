/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.List;

import org.junit.Test;

import com.ean.mobile.Destination;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class DestinationLookupIntTest {

    @Test
    public void testGetDestinationInfos() throws Exception {
        DestinationLookup destinationLookup = new DestinationLookup("sea");
        List<Destination> results = RequestProcessor.run(destinationLookup);
        assertThat(results.size(), greaterThan(0));
    }
}
