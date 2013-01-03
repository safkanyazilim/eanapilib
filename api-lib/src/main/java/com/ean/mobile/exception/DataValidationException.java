/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.exception;

/**
 * The exception that is thrown when the API returns a DataValidationException.
 * Usually indicates a problem with the data passed to the API, often date issues.
 */
public final class DataValidationException extends EanWsError {
    /**
     * The main constructor. The verbose message is used as the exception's main message.
     * @param verboseMessage The message for the exception.
     * @param category The "category" of the error.
     * @param presentationMessage The localized, simplified version of verboseMessage.
     */
    public DataValidationException(final String verboseMessage,
                                   final String category,
                                   final String presentationMessage) {
        super(verboseMessage, category, presentationMessage);
    }
}
