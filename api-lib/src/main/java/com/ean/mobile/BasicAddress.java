/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * A basic implementation of address. Simply a final implementation of the abstract {@link Address} with no extra
 * fields or constructors.
 */
public final class BasicAddress extends Address {


    public BasicAddress(final String addressLine1,
                        final String city,
                        final String stateProvinceCode,
                        final String countryCode,
                        final String postalCode) {
        this(Arrays.asList(addressLine1), city, stateProvinceCode, countryCode, postalCode);
    }

    public BasicAddress(final List<String> lines,
                        final String city,
                        final String stateProvinceCode,
                        final String countryCode,
                        final String postalCode) {
        super(lines, city, stateProvinceCode, countryCode, postalCode);

    }

    public BasicAddress(final JSONObject object) {
        super(object);
    }
}
