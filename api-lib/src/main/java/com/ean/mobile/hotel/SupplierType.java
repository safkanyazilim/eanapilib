/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel;

import java.util.Collections;

import java.util.HashSet;
import java.util.Set;

/**
 * This class wraps supplier types so that we can univerally references them
 * as opposed to dealing with ids and code as the API returns them.
 *
 * Id's come from http://developer.ean.com/docs/read/request_itinerary
 * Letters come from http://developer.ean.com/docs/read/room_avail
 */
public enum  SupplierType {

    /**
     * Expedia supplier type.
     */
    EXPEDIA("E", 2, 9, 13),

    /**
     * Sabre supplier type.
     */
    SABRE("S", 3),

    /**
     * Venere supplier type.
     */
    VENERE("V", 14),

    /**
     * Worldspan supplier type.
     */
    WORLDSPAN("W", 10);

    /**
     * Single letter code for supplier type.
     */
    public final String code;

    /**
     * Set of ids used for supplier type.
     */
    private final Set<Integer> ids;

    /**
     * Constructor used to create one.
     * @param code Letter code for supplier.
     * @param ids Set of supplier ids.
     */
    private SupplierType(final String code, final Integer... ids) {
        final Set<Integer> idSet = new HashSet<Integer>(ids.length);
        Collections.addAll(idSet, ids);
        this.ids = Collections.unmodifiableSet(idSet);

        this.code = code;
    }

    /**
     * Looks for a matching supplier for a supplier id.
     * @param supplierId Supplier id passed from the api.
     * @return SupplierType if there was a match, otherwise null.
     */
    public static SupplierType getById(final int supplierId) {
        for (SupplierType supplierType : values()) {
            if (supplierType.ids.contains(supplierId)) {
                return supplierType;
            }
        }
        return null;
    }

    /**
     * Looks for a matching supplier for a supplier code.
     * @param supplierCode Supplier code passed from the api.
     * @return SupplierType if there was a match, otherwise null.
     */
    public static SupplierType getByCode(final String supplierCode) {
        for (SupplierType supplierType : values()) {
            if (supplierType.code.equals(supplierCode)) {
                return supplierType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", this.name(), this.code);
    }
}
