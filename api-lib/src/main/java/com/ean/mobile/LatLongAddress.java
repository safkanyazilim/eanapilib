package com.ean.mobile;

import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * An address implementation that has latitude and longitude included.
 */
public final class LatLongAddress extends Address {

    /**
     * The latitude of the address.
     */
    public final BigDecimal latitude;

    /**
     * The longitude for this address.
     */
    public final BigDecimal longitude;

    /**
     * Code for accuracy of the provided coordinates. May be null.
     */
    public final String coordinateAccuracyCode;

    /**
     * Creates an address object which includes latitude and longitude parts.
     * @param object A JSONObject which has all of the necessary fields for this type of object.
     */
    public LatLongAddress(final JSONObject object) {
        super(object);
        //Defaults to 0,0 lat,long to prevent null pointer exceptions.
        this.latitude = new BigDecimal(object.optString("latitude", "0"));
        this.longitude = new BigDecimal(object.optString("longitude", "0"));
        this.coordinateAccuracyCode = object.optString("coordinateAccuracyCode");
    }
}
