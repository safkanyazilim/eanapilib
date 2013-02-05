/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Collections;
import java.util.List;

/**
 * The holder for a list of hotels, used as the return value from the list call.
 */
public final class HotelList {

    /**
     * The key used to paginate through multiple parts of a larger list request.
     */
    public final String cacheKey;

    /**
     * The server where the cached request lies.
     */
    public final String cacheLocation;

    /**
     * The ID also used to paginate through cached requests.
     */
    public final String customerSessionId;

    /**
     * The total number of Hotel objects able to be retrieved by the request that started this.
     */
    public final int totalNumberOfResults;

    /**
     * Holds the hotel info objects from the api response.
     */
    public final List<Hotel> hotels;

    /**
     * The main constructor for this class. Maps to ArrayList(Collection c) and sets the other fields
     * of this class as well.
     * @param hotels The hotels with which to initially populate this list.
     * @param cacheKey The cache key to set.
     * @param cacheLocation The cache location to set.
     * @param customerSessionId The session to set.
     * @param totalNumberOfResults The total number of results that the request that created this
     *                             HotelList can return.
     */
    public HotelList(final List<Hotel> hotels,
                     final String cacheKey, final String cacheLocation,
                     final String customerSessionId, final int totalNumberOfResults) {
        this.hotels = Collections.unmodifiableList(hotels);
        this.cacheKey = cacheKey;
        this.cacheLocation = cacheLocation;
        this.customerSessionId = customerSessionId;
        this.totalNumberOfResults = totalNumberOfResults;
    }

    /**
     * Similar to {@link java.util.Collections#emptyList()} but for HotelList.
     * @return An empty hotel info list whose fields are all null.
     */
    public static HotelList emptyList() {
        return new HotelList(Collections.<Hotel>emptyList(), null, null, null, 0);
    }
}
