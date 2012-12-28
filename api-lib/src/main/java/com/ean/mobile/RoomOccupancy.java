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
     * The maximum number of children specifiable by the numberOfChildren parameter of
     * {@link RoomOccupancy#RoomOccupancy(int, int)}.
     */
    private static final int MAX_CHILDREN = 20;

    /**
     * The hash code calculated at construction.
     */
    private final int hashCode;

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

        // now precaluclate the hashcode
        final int primeNumber = 31;
        int hashCode = numberOfAdults == 0 ? 1 : numberOfAdults;
        hashCode *= primeNumber * this.childAges.size();
        this.hashCode = hashCode;

    }

    /**
     * This constructor builds a new room occupancy using simple numbers of children and sets
     * {@link RoomOccupancy#childAges} to be a list of 0s, as long as numberOfChildren, with a maximum size of 20
     * to prevent memory overloads.
     * Practically, number of children shouldn't exceed 5, nor should the sum of adults and children exceed 5, although
     * some leeway is given.
     * @param numberOfAdults The number of adults in this occupancy.
     * @param numberOfChildren The number of children in this occupancy.
     */
    public RoomOccupancy(final int numberOfAdults, final int numberOfChildren) {
        //We don't want to make too large of a list, so we limit the numberOfChildren to 20.
        this(numberOfAdults, Collections.nCopies(Math.min(MAX_CHILDREN, numberOfChildren), 0));
    }

    /**
     * A constructor to upgrade a RoomOccupancy to have the particular child ages specified. This is intended
     * to upgrade a RoomOccupancy object constructed with the {@link RoomOccupancy#RoomOccupancy(int, int)} constructor
     * to have the full list of childAges associated.
     * @param baseOccupancy The base occupancy.
     * @param childAges The children's ages to integrate.
     */
    public RoomOccupancy(final RoomOccupancy baseOccupancy, final List<Integer> childAges) {
        this(baseOccupancy.numberOfAdults, childAges);
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

    /**
     * A pair of room occupancies which have the same NUMBER of children and the same NUMBER of adults are declared
     * to be equal.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof RoomOccupancy && this.hashCode() == obj.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hashCode;
    }
}
