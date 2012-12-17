/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Collections;
import java.util.List;

/**
 * The rest of the information about a hotel, that is loaded using the InformationRequest.
 */
public final class HotelInfoExtended {

    /**
     * The id of the hotel this extended information is associated with.
     */
    public final long hotelId;

    /**
     * The long description of this hotel.
     */
    public final String longDescription;

    /**
     * The list of hotel images for this hotel.
     */
    public final List<HotelImageTuple> images;

    /**
     * The sole constructor, enables the class to be immutable.
     * @param hotelId The id of the hotel that this information is for.
     * @param longDescription The long description of this hotel. Often contains embedded html.
     * @param images The list of images for this hotel.
     */
    public HotelInfoExtended(final long hotelId, final String longDescription, final List<HotelImageTuple> images) {
        this.hotelId = hotelId;
        this.longDescription = longDescription;
        this.images = Collections.unmodifiableList(images);
    }

}
