/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import android.graphics.drawable.Drawable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HotelImageTupleTest {

    @Test
    public void testThumbnailLoaded() {
        HotelImageTuple tuple = new HotelImageTuple(null, null, null);

        assertNull(tuple.thumbnailUrl);
        assertNull(Whitebox.getInternalState(tuple, "thumbnail"));
        assertFalse(tuple.isThumbnailLoaded());

    }

    @Test
    public void testMainLoaded() {
        HotelImageTuple tuple = new HotelImageTuple(null, null, null);

        assertNull(tuple.mainUrl);
        assertNull(Whitebox.getInternalState(tuple, "main"));
        assertFalse(tuple.isMainImageLoaded());

        Whitebox.setInternalState(tuple, "main", new Drawable());
        assertTrue(tuple.isMainImageLoaded());
    }


}
