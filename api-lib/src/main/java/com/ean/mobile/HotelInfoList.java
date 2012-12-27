/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The holder for a list of hotels, used as the return value from the list call.
 */
public final class HotelInfoList extends ArrayList<HotelInfo> {

    /**
     * The key used to paginate through multiple parts of a larger list request.
     */
    public final String cacheKey;

    /**
     * The server where the cached request lies.
     * TODO: Determine necessity of this field.
     */
    public final String cacheLocation;

    /**
     * The ID also used to paginate through cached requests.
     */
    public final String customerSessionId;

    /**
     * The main constructor for this class. Maps to ArrayList(Collection c) and sets the other fields
     * of this class as well.
     * @param hotelInfos The HotelInformations with which to initially populate this list.
     * @param cacheKey The cache key to set.
     * @param cacheLocation The cache location to set.
     * @param customerSessionId The session to set.
     */
    public HotelInfoList(final Collection<HotelInfo> hotelInfos,
                         final String cacheKey,
                         final String cacheLocation,
                         final String customerSessionId) {
        super(hotelInfos);
        this.cacheKey = cacheKey;
        this.cacheLocation = cacheLocation;
        this.customerSessionId = customerSessionId;
    }

    /**
     * Similar to {@link java.util.Collections#emptyList()} but for HotelInfoList.
     * @return An empty hotel info list whose fields are all null.
     */
    public static HotelInfoList empty() {
        return new HotelInfoList(Collections.<HotelInfo>emptyList(), null, null, null);
    }
}
