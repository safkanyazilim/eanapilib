/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import java.util.ArrayList;
import java.util.Collection;

public final class HotelInfoList extends ArrayList<HotelInfo> {
    public final String cacheKey;
    public final String cacheLocation;
    public final String customerSessionId;

    public HotelInfoList(final Collection<HotelInfo> c,
                         final String cacheKey,
                         final String cacheLocation,
                         final String customerSessionId) {
        super(c);
        this.cacheKey = cacheKey;
        this.cacheLocation = cacheLocation;
        this.customerSessionId = customerSessionId;
    }
}
