/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.exception;

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
