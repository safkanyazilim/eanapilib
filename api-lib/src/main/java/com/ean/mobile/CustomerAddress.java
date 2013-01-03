/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.json.JSONObject;

import java.util.List;

public final class CustomerAddress extends Address {
    public enum AddressType {
        UNKNOWN(Integer.MIN_VALUE),
        BILLING(1),
        SHIPPING(2);

        public final int typeId;

        AddressType(final int typeId) {
            this.typeId = typeId;
        }

        public static AddressType fromInt(final int typeId) {
            if (typeId > 2 || typeId < 1) {
                return UNKNOWN;
            }
            if (typeId == 1) {
                return BILLING;
            } else {
                return SHIPPING;
            }
        }
    }


    /**
     * Whether or not this address is a primary address associated with a customer.
     */
    public final boolean isPrimary;

    /**
     * Whether this is a billing or shipping address.
     */
    public final AddressType type;

    public CustomerAddress(final JSONObject object) {
        super(object);
        this.isPrimary = object.optBoolean("isPrimary");
        this.type = AddressType.fromInt(object.optInt("type"));
    }
}