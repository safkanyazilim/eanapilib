/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * The number of HotelInfo object in a "page" per request.
     */
    public final int pageSize;

    /**
     * The total number of HotelInfo objects able to be retrieved by the request that started this.
     */
    public final int totalNumberOfResults;

    /**
     * The number of pages either loaded or in process of being loaded.
     */
    private int numberOfPages = 0;

    /**
     * The main constructor for this class. Maps to ArrayList(Collection c) and sets the other fields
     * of this class as well.
     * @param hotelInfos The HotelInformations with which to initially populate this list.
     * @param cacheKey The cache key to set.
     * @param cacheLocation The cache location to set.
     * @param customerSessionId The session to set.
     */
    public HotelInfoList(final List<HotelInfo> hotelInfos,
                         final String cacheKey,
                         final String cacheLocation,
                         final String customerSessionId,
                         final int pageSize,
                         final int totalNumberOfResults) {
        super(hotelInfos);
        this.cacheKey = cacheKey;
        this.cacheLocation = cacheLocation;
        this.customerSessionId = customerSessionId;
        this.pageSize = pageSize;
        this.totalNumberOfResults = totalNumberOfResults;
    }

    /**
     * Similar to {@link java.util.Collections#emptyList()} but for HotelInfoList.
     * @return An empty hotel info list whose fields are all null.
     */
    public static HotelInfoList empty() {
        return new HotelInfoList(Collections.<HotelInfo>emptyList(), null, null, null, 0, 0);
    }

    public int allocateNewPageIndex() {
        numberOfPages++;
        return numberOfPages;
    }

    public int getCurrentPageIndex() {
        return numberOfPages;
    }
}
