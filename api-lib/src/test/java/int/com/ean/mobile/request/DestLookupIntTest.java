/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.util.List;

import org.junit.Test;

import com.ean.mobile.Destination;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class DestLookupIntTest {

    @Test
    public void testGetDestInfos() throws Exception {
        final String destination = "sea";
        List<Destination> results = DestLookup.getDestInfos(destination);
        assertThat(results.size(), greaterThan(0));
    }
}
