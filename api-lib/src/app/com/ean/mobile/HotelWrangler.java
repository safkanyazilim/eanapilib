/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class handles all of the loaded hotels, as well as customer session information and searches.
 * It extends Application so its data can be used globally.
 */
public class HotelWrangler {
    private final List<HotelInfo> infos = new ArrayList<HotelInfo>();
    private HotelInfo selectedInfo;

    private String customerSessionId;
    private String cacheLocation;
    private String cacheKey;
    private String searchQuery;

    private Date arrivalDate;
    private Date departureDate;

    private Integer numberOfRooms = 1;
    private Integer numberOfAdults = 2;
    private Integer numberOfChildren = 0;

    private HotelRoom selectedRoom;

    public List<HotelInfo> getInfos() {
        return infos;
    }

    public HotelWrangler setInfos(final List<HotelInfo> infos) {
        this.infos.clear();
        this.infos.addAll(infos);
        this.selectedInfo = null;
        return this;
    }

    public HotelInfo getSelectedInfo() {
        return this.selectedInfo;
    }

    public HotelWrangler setSelectedInfo(final HotelInfo selectedInfo) {
        this.selectedInfo = selectedInfo;
        return this;
    }

    public HotelWrangler setCustomerSessionId(final String id) {
        this.customerSessionId = id;
        return this;
    }

    public String getCustomerSessionId() {
        return this.customerSessionId;
    }

    public HotelWrangler setCacheKey(final String id) {
        this.cacheKey = id;
        return this;
    }

    public String getCacheKey() {
        return this.cacheKey;
    }

    public HotelWrangler setCacheLocation(final String id) {
        this.cacheLocation = id;
        return this;
    }

    public String getCacheLocation() {
        return this.cacheLocation;
    }

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
