/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.HashMap;
import com.ean.mobile.hotel.HotelImageTuple;

/**
 * Extends map in such a way that if the HotelImageDrawable that has been requested by the HotelImageTuple
 * does not exist in the map, it will create a new drawable based on that tuple.
 */
public final class HotelImageDrawableMap extends HashMap<HotelImageTuple, HotelImageDrawable> {
    @Override
    public HotelImageDrawable get(Object key) {
        if (!(key instanceof HotelImageTuple)) {
            return null;
        }
        HotelImageTuple tuple = (HotelImageTuple) key;
        if(!containsKey(tuple)) {
            put(tuple, new HotelImageDrawable(tuple));
        }
        return super.get(key);
    }
}
