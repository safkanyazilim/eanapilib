/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.exception;

/**
 * Thrown by the API if there is a problem creating a uri with which to perform a request. Usually caused
 * by invalid characters being passed to requests.
 */
public final class UriCreationException extends RuntimeException {
    /**
     * The main constructor.
     * @param message The message to pass.
     * @param cause The cause of this error, if applicable.
     */
    public UriCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
