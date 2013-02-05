package com.ean.mobile;

import java.util.HashMap;

import com.ean.mobile.hotel.HotelImageTuple;

public class HotelImageDrawableMap extends HashMap<HotelImageTuple, HotelImageDrawable> {
    @Override
    public HotelImageDrawable get(Object key) {
        if (!(key instanceof HotelImageTuple)) {
            return null;
        }
        HotelImageTuple tuple = (HotelImageTuple) key;
        if(!containsKey(tuple)) {
            put(tuple, new HotelImageDrawable(tuple));
        }
        return super.get(key);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
