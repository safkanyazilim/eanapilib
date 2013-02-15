/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

/**
 * A basic (final) implementation of address. Simply a final implementation of the abstract {@link Address}
 * with no extra fields or constructors, but is a concrete and immutable implementation.
 */
public final class BasicAddress extends Address {
    /**
     * The standard constructor for this Address, maps directly to the similar constructor for {@link Address}.
     * @param object The JSONObject to construct this address from.
     */
    public BasicAddress(final JSONObject object) {
        super(object);
    }

    /**
     * Creates a basic address object from the various parameters.
     * @param addressLine1 First line of address.
     * @param city City of address.
     * @param stateProvinceCode State/province code for state.
     * @param countryCode ISO country code for country.
     * @param postalCode Postal code for country.
     */
    public BasicAddress(final String addressLine1, final String city, final String stateProvinceCode,
            final String countryCode, final String postalCode) {
        this(Collections.singletonList(addressLine1), city, stateProvinceCode, countryCode, postalCode);
    }

    /**
     * Creates a basic address object from the various parameters.
     * @param addressLines The lines of the address.
     * @param city City of address.
     * @param stateProvinceCode State/province code for state.
     * @param countryCode ISO country code for country.
     * @param postalCode Postal code for country.
     */
    public BasicAddress(final List<String> addressLines, final String city, final String stateProvinceCode,
            final String countryCode, final String postalCode) {
        super(addressLines, city,  stateProvinceCode, countryCode, postalCode);
    }
}
