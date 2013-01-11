/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

/**
 * Container for address information. Should handle any international address.
 * If the address is invalid in some way, the validationErrors list will delineate in what way it is invalid. This
 * is in lieu of throwing exceptions since there are potentially unaccounted for address types which may be stored
 * with this object, and there's no need to throw exceptions for partial addresses.
 */
public abstract class Address {

    /**
     * The country codes wherein a state code is supported and required.
     */
    public static final List<String> VALID_STATE_PROVINCE_CODE_COUNTRY_CODES
            = Collections.unmodifiableList(Arrays.asList("US", "CA", "AU"));

    /**
     * An ordered list of address lines, starting with address line 1 and maxing out around 3.
     */
    public final List<String> lines;

    /**
     * The city associated with this address.
     */
    public final String city;

    /**
     * The two-character code for the state/province containing the specified city. Only valid for cities in Australia,
     * Canada, and The United States.
     */
    public final String stateProvinceCode;

    /**
     * Two-character <a href="http://www.iso.org/iso/home/standards/country_codes/iso-3166-1_decoding_table.htm">
     *     ISO-3166</a> code for the country of this address.
     */
    public final String countryCode;

    /**
     * The postal code associated with this address. Will accept any international format.
     */
    public final String postalCode;

    /**
     * The cached representation of this object as a string. Used to prevent the string from needing to be constructed
     * every time the toString method is called.
     */
    private transient String asString;

    /**
     * Creates an address object from a JSONObject which has the appropriate address fields.
     * @param object The JSONObject from which to construct the address.
     */
    public Address(final JSONObject object) {
        final List<String> localLines = new LinkedList<String>();
        String line;
        for (int i = 1; localLines.size() == i - 1; i++) {
            line = object.optString("address" + i);
            if (line != null && !"".equals(line)) {
                localLines.add(line);
            }
        }

        this.lines = Collections.unmodifiableList(localLines);
        this.city = object.optString("city");
        this.stateProvinceCode = object.optString("stateProvinceCode");
        this.countryCode = object.optString("countryCode");
        this.postalCode = object.optString("postalCode");
    }

    /**
     * Creates an address object from the various parameters.
     * @param addressLine1 First line of address.
     * @param city City of address.
     * @param stateProvinceCode State/province code for state.
     * @param countryCode ISO country code for country.
     * @param postalCode Postal code for country.
     */
    public Address(final String addressLine1, final String city, final String stateProvinceCode,
            final String countryCode, final String postalCode) {
        lines = Collections.unmodifiableList(Collections.singletonList(addressLine1));
        this.city = city;
        this.stateProvinceCode = stateProvinceCode;
        this.countryCode = countryCode;
        this.postalCode = postalCode;
    }

    /**
     * Gets NameValuePairs for the address information so it can be sent in a rest request.
     * @return The requested NameValuePairs
     */
    public List<NameValuePair> asBookingRequestPairs() {
        final List<NameValuePair> addressPairs = new LinkedList<NameValuePair>();
        int i = 1;
        for (String line : lines) {
            addressPairs.add(new BasicNameValuePair("address" + i, line));
            i++;
        }
        addressPairs.add(new BasicNameValuePair("city", city));
        if (VALID_STATE_PROVINCE_CODE_COUNTRY_CODES.contains(countryCode)) {
            addressPairs.add(new BasicNameValuePair("stateProvinceCode", stateProvinceCode));
        }
        addressPairs.add(new BasicNameValuePair("countryCode", countryCode));
        addressPairs.add(new BasicNameValuePair("postalCode", postalCode));

        return Collections.unmodifiableList(addressPairs);
    }

    /**
     * Returns a formatted string representing this address as it would appear on an envelope.
     * @return The formatted address.
     */
    @Override
    public String toString() {
        final StringBuilder addressBuilder = new StringBuilder();
        for (String line : lines) {
            addressBuilder.append(line);
            addressBuilder.append("\n");
        }
        addressBuilder.append(city);
        addressBuilder.append(" ");
        addressBuilder.append(stateProvinceCode);
        addressBuilder.append(" ");
        addressBuilder.append(postalCode);
        addressBuilder.append("\n");
        addressBuilder.append(countryCode);
        asString = addressBuilder.toString();
        return asString;
    }

}

