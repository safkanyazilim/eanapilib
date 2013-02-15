/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel;

/**
 * Represents a cancellation response as returned from the API.
 */
public class Cancellation {

    /**
     * Reference number for the cancellation, if successful.
     */
    public final String cancellationNumber;

    /**
     * Constructs a Cancellation object from a cancellation number.
     * @param cancellationNumber the cancellation number to use.
     */
    public Cancellation(final String cancellationNumber) {
        this.cancellationNumber = cancellationNumber;
    }

}
