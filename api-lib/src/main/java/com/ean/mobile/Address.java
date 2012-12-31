package com.ean.mobile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
     * The maximum number of supported address lines, excluding state, country, and postal code information.
     */
    public static final int MAX_NUM_SUPPORTED_LINES = 3;

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


    public final List<String> validationErrors;


    public Address(final String addressLine1,
                   final String city,
                   final String countryCode,
                   final String postalCode) {
        this(Arrays.asList(addressLine1), city, countryCode, postalCode);
    }

    public Address(final List<String> addressLines,
                   final String city,
                   final String countryCode,
                   final String postalCode) {
        this(addressLines, city, null, countryCode,  postalCode);
    }

    public Address(final String addressLine1,
                   final String city,
                   final String stateProvinceCode,
                   final String countryCode,
                   final String postalCode) {
        this(Arrays.asList(addressLine1), city, stateProvinceCode, countryCode, postalCode);
    }

    public Address(final List<String> lines,
                   final String city,
                   final String stateProvinceCode,
                   final String countryCode,
                   final String postalCode) {
        this.lines = Collections.unmodifiableList(lines == null ? Collections.<String>emptyList() : lines);
        this.city = city;
        this.stateProvinceCode = stateProvinceCode;
        this.countryCode = countryCode;
        this.postalCode = postalCode;



        this.validationErrors = Collections.unmodifiableList(validate());

    }

    public Address(final JSONObject object) {
        final List<String> lines = new LinkedList<String>();
        String line;
        // This is an infinite loop that only exits when an address line is not found.
        for (int i = 1; true; i++) {
            line = object.optString("address" + i);
            if (line == null) {
                break;
            }
            lines.add(line);
        }

        this.lines = Collections.unmodifiableList(lines);
        this.city = object.optString("city");
        this.stateProvinceCode = object.optString("stateProvinceCode");
        this.countryCode = object.optString("countryCode");
        this.postalCode = object.optString("postalCode");

        this.validationErrors = Collections.unmodifiableList(validate());
    }

    private List<String> validate() {
        final List<String> validationErrors = new ArrayList<String>();
        //now we validate this address.
        if (this.lines.size() < 1) {
            validationErrors.add("Address must have at least one line for the street address.");
        } else if (this.lines.size() > MAX_NUM_SUPPORTED_LINES) {
            validationErrors.add("Address may have too many lines. Typical addresses have a maximum of 3 lines.");
        }

        if (city == null) {
            validationErrors.add("City cannot be null.");
        }

        if (VALID_STATE_PROVINCE_CODE_COUNTRY_CODES.contains(countryCode)) {
            if (stateProvinceCode == null || stateProvinceCode.length() != 2) {
                validationErrors.add("A 2 character state province code is required for AU, CA, and US country codes.");
            }
        } else if (stateProvinceCode != null) {
            validationErrors.add("stateProvinceCode is only valid for AU, CA, and US country codes.");
        }

        if (countryCode == null || countryCode.length() != 2) {
            validationErrors.add("2 character country code is required.");
        }

        if (postalCode == null) {
            validationErrors.add("A postal code is required.");
        }

        return validationErrors;
    }

    public boolean hasValidationErrors() {
        return validationErrors.size() > 0;
    }

    /**
     * Gets NameValuePairs for the address information so it can be sent in a rest request.
     * @return The requested NameValuePairs
     */
    public List<NameValuePair> asBookingRequestPairs() {
        List<NameValuePair> addressPairs = new ArrayList<NameValuePair>();
        for (int i = 0; i < lines.size(); i++) {
            addressPairs.add(new BasicNameValuePair("address" + i, lines.get(i)));
        }
        addressPairs.add(new BasicNameValuePair("city", city));
        if (VALID_STATE_PROVINCE_CODE_COUNTRY_CODES.contains(countryCode)) {
            addressPairs.add(new BasicNameValuePair("stateProvinceCode", stateProvinceCode));
        }
        addressPairs.add(new BasicNameValuePair("countryCode", countryCode));
        addressPairs.add(new BasicNameValuePair("postalCode", postalCode));

        return Collections.unmodifiableList(addressPairs);
    }

}

