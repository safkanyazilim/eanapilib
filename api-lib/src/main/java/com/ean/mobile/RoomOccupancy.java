/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Collections;
import java.util.List;

/**
 * The holder for information about a particular room's adult and child occupancy.
 */
public final class RoomOccupancy {

    /**
     * The number of adults expected to occupy the room.
     */
    public final int numberOfAdults;

    /**
     * The list of children's ages that will be occupying said room.
     */
    public final List<Integer> childAges;

    /**
     * The primary constructor setting the final variables in this class.
     * @param numberOfAdults The number of adults in this occupancy.
     * @param childAges The list of children's ages for this room.
     */
    public RoomOccupancy(final int numberOfAdults, final List<Integer> childAges) {
        this.numberOfAdults = numberOfAdults;
        this.childAges
                = childAges == null
                ? Collections.<Integer>emptyList()
                : Collections.unmodifiableList(childAges);
    }


    /**
     * Gets this object as a string in the abbreviated form required by the rest requests of the api.
     * @return The string formatted as follows: [numberOfAdults],[childAge],[childAge],... where each child age
     * is printed.
     */
    public String asAbbreviatedRequestString() {
        final StringBuilder adultsAndChildren = new StringBuilder((childAges.size() * 2) + 1);
        adultsAndChildren.append(numberOfAdults);
        for (int childAge : childAges) {
            adultsAndChildren.append(",");
            adultsAndChildren.append(childAge);
        }
        return adultsAndChildren.toString();
    }
}
