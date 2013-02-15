/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.exception;

/**
 * Thrown if there is an unrecoverable problem with parameter values that will cause requests to fail.
 */
public class CommonParameterValidationException extends RuntimeException {

    /**
     * Primary constructor.
     *
     * @param message a brief message that describes the problem.
     */
    public CommonParameterValidationException(final String message) {
        super(message);
    }
}
