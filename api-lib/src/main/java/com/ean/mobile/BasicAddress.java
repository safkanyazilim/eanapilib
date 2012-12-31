package com.ean.mobile;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public final class BasicAddress extends Address {
    public BasicAddress(final String addressLine1,
                        final String city,
                        final String countryCode,
                        final String postalCode) {
        this(Arrays.asList(addressLine1), city, countryCode, postalCode);
    }

    public BasicAddress(final List<String> addressLines,
                        final String city,
                        final String countryCode,
                        final String postalCode) {
        this(addressLines, city, null, countryCode,  postalCode);
    }

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

    public BasicAddress(final JSONObject object){
        super(object);
    }
}
