/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.exception;

/**
 * An exception thrown when a request was unexpectedly redirected while executing, denoting  a network sign on
 * or connectivity issue.
 */
public final class UrlRedirectionException extends Exception {

    /**
     * Sets the exception's message to note that the connection was redirected and is likely a connection issue.
     */
    public UrlRedirectionException() {
        this("The connection was redirected unexpectedly. Likely a network sign on/connectivity issue.");
    }

    /**
     * Sets the exception's message to whatever was passed in.
     * @param message The exception's message
     */
    public UrlRedirectionException(final String message) {
        super(message);
    }
}
