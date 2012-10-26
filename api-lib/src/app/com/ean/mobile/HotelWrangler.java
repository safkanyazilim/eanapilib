/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import android.app.Application;

import java.util.Date;
import java.util.List;

public class HotelWrangler extends Application {
    private List<HotelInfo> infos;
    private HotelInfo selectedInfo;

    private String
        customerSessionId,
        cacheLocation,
        cacheKey,
        searchQuery;

    private Date arrivalDate, departureDate;

    private Integer
        numberOfRooms = 1,
        numberOfAdults = 2,
        numberOfChildren = 0;

    private HotelRoom selectedRoom;


    public List<HotelInfo> getInfos(){
        return infos;
    }

    public HotelWrangler setInfos(List<HotelInfo> infos){
        this.infos = infos;
        this.selectedInfo = null;
        return this;
    }

    public HotelInfo getSelectedInfo(){
        return this.selectedInfo;
    }

    public HotelWrangler setSelectedInfo(HotelInfo selectedInfo){
        this.selectedInfo = selectedInfo;
        return this;
    }

    public HotelWrangler setCustomerSessionId(String id){
        this.customerSessionId = id;
        return this;
    }

    public String getCustomerSessionId() {
        return this.customerSessionId;
    }

    public HotelWrangler setCacheKey(String id){
        this.cacheKey = id;
        return this;
    }

    public String getCacheKey() {
        return this.cacheKey;
    }

    public HotelWrangler setCacheLocation(String id){
        this.cacheLocation = id;
        return this;
    }

    public String getCacheLocation() {
        return this.cacheLocation;
    }

    public HotelWrangler setSearchQuery(String searchQuery){
        this.searchQuery = searchQuery;
        return this;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String getSearchQueryDisplay() {
        return searchQuery;
    }

    public HotelWrangler setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
        return this;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public HotelWrangler setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
        return this;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public HotelRoom getSelectedRoom() {
        return selectedRoom;
    }

    public HotelWrangler setSelectedRoom(HotelRoom selectedRoom) {
        this.selectedRoom = selectedRoom;
        return this;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(Integer numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public Integer getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(Integer numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }
}
