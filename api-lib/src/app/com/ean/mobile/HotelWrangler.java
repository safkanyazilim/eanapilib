/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Date;

/**
 * This class handles all of the loaded hotels, as well as customer session information and searches.
 * It extends Application so its data can be used globally.
 */
public final class HotelWrangler {
    private String searchQuery;

    private Date arrivalDate;
    private Date departureDate;

    private Integer numberOfRooms = 1;
    private Integer numberOfAdults = 2;
    private Integer numberOfChildren = 0;

    private HotelRoom selectedRoom;

    public HotelWrangler setSearchQuery(final String searchQuery) {
        this.searchQuery = searchQuery;
        return this;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getSearchQueryDisplay() {
        return searchQuery;
    }

    public HotelWrangler setArrivalDate(final Date arrivalDate) {
        this.arrivalDate = arrivalDate;
        return this;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public HotelWrangler setDepartureDate(final Date departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public HotelRoom getSelectedRoom() {
        return selectedRoom;
    }

    public HotelWrangler setSelectedRoom(final HotelRoom selectedRoom) {
        this.selectedRoom = selectedRoom;
        return this;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(final Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(final Integer numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public Integer getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(final Integer numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }
}
