/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.exception;

import org.json.JSONObject;

/**
 * A wrapper for the EanWsError object that can come back in the case of a failure in the request.
 */
public class EanWsError extends Exception {

    /**
     * The category of error. Often "RECOVERABLE".
     */
    public final String category;

    /**
     * The localized, plain-speak version of the error's main message.
     */
    public final String presentationMessage;

    /**
     * The main constructor. The verbose message is used as the exception's main message.
     * @param verboseMessage The message for the exception.
     * @param category The "category" of the error.
     * @param presentationMessage The localized, simplified version of verboseMessage.
     */
    public EanWsError(final String verboseMessage, final String category, final String presentationMessage) {
        super(verboseMessage);
        this.category = category;
        this.presentationMessage = presentationMessage;
    }

    /**
     * Gets an EanWsError object from a representative JSONObject.
     * @param error The error that has occurred in JSON.
     * @return The java exception that the error's object implies.
     */
    public static EanWsError fromJson(final JSONObject error) {
        if (error.has("category") && "DATA_VALIDATION".equals(error.optString("category"))) {
            return new DataValidationException(
                error.optString("verboseMessage"),
                error.optString("category"),
                error.optString("presentationMessage"));
        }
        return new EanWsError(
            error.optString("verboseMessage"),
            error.optString("category"),
            error.optString("presentationMessage"));
    }
}
