/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ImageFetcherTest {

    @Test
    public void testGetFullImageUrlNull() throws Exception {
        assertNull(ImageFetcher.getFullImageUrl(null));
    }

    @Test
    public void testGetFullImageUrlNonsense() throws Exception {
        assertEquals("http://images.travelnow.comnonsense", ImageFetcher.getFullImageUrl("nonsense").toString());
    }
}
