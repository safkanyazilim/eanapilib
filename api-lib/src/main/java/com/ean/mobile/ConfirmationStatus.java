/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The various statuses a HotelConfirmation can hold once booked.
 * Indicates the status of the reservation in the supplier system at the time of booking.
 * Anticipate appropriate customer messaging for all non-confirmed values.
 */
public enum ConfirmationStatus {

    /**
     * Encountered when an unknown confirmation status is encountered. Should never happen.
     */
    UNKNOWN(""),
    /**
     * The normal state of a confirmation. When everything has gone right, a confirmation will say confirmed.
     */
    CONFIRMED("CF"),
    /**
     * A cancelled confirmation.
     */
    CANCELLED("CX"),
    /**
     * Unconfirmed. Usually due to the property being sold out. Agent will follow up.
     * Most cases result in customer being advised to select other property when agent cannot obtain a reservation.
     */
    UNCONFIRMED("UC"),
    /**
     * Pending Supplier. Agent will follow up with customer when confirmation number is available.
     */
    PENDING_SUPPLIER("PS"),
    /**
     * Error. Agent attention needed. Agent will follow up.
     */
    ERROR("ER"),
    /**
     * Deleted Itinerary (Usually a test or failed booking).
     */
    DELETED("DT");

    private static final Map<String, ConfirmationStatus> STATUSES;

    static {
        final Map<String, ConfirmationStatus> statuses = new HashMap<String, ConfirmationStatus>();
        statuses.put(CONFIRMED.code, CONFIRMED);
        statuses.put(CANCELLED.code, CANCELLED);
        statuses.put(UNCONFIRMED.code, UNCONFIRMED);
        statuses.put(PENDING_SUPPLIER.code, PENDING_SUPPLIER);
        statuses.put(ERROR.code, ERROR);
        statuses.put(DELETED.code, DELETED);
        STATUSES = Collections.unmodifiableMap(statuses);
    }

    final String code;

    /**
     * The sole constructor, sets the string code that this status applies to.
     * @param code The code for this status.
     */
    ConfirmationStatus(final String code) {
        this.code = code;
    }

    /**
     * Gets a ConfirmationStatus object from a string. Assumes no whitespace and fully uppercase.
     * @param code The code returned from the API
     * @return The ConfirmationStatus object represented by the string.
     */
    public static ConfirmationStatus fromString(final String code) {
        if (code == null || code.length() != 2) {
            return UNKNOWN;
        } else {
            return STATUSES.get(code);
        }
    }
}
