/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.json.JSONObject;

/**
 * An address specifically suited for customer addresses, with some functionality specifying
 * the use of the address for the customer.
 */
public final class CustomerAddress extends Address {

    /**
     * The type of address that this address is for, Billing or Shipping.
     */
    public enum AddressType {
        /**
         * Should only happen if there is a new type of address.
         */
        UNKNOWN(Integer.MIN_VALUE),

        /**
         * What will be shown for a billing address.
         */
        BILLING(1),

        /**
         * If the address is a shipping address.
         */
        SHIPPING(2);

        /**
         * The id returned from the api that this object will be for.
         */
        public final int typeId;

        /**
         * The sole constructor for this enum.
         * @param typeId The id returned from the api.
         */
        AddressType(final int typeId) {
            this.typeId = typeId;
        }

        /**
         * Returns the appropriate address type from a particular integer.
         * @param typeId The id to find an address type.
         * @return The appropriate address type.
         */
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

    /**
     * The JSON-based constructor for this address. In addition to the normal address fields,
     * the json object must also have fields "isPrimary" and "type" as boolean and integer,
     * respectively.
     * @param object The JSONObject holding the appropriate fields.s
     */
    public CustomerAddress(final JSONObject object) {
        super(object);
        this.isPrimary = object.optBoolean("isPrimary");
        this.type = AddressType.fromInt(object.optInt("type"));
    }
}