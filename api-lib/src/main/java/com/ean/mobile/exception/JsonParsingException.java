/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.exception;

import org.json.JSONException;

/**
 * This exception will wrap json exceptions since it doesn't make sense for consumers of the library to
 * have to deal with json exceptions when they should never see them if the library is coded correctly.
 */
public class JsonParsingException extends RuntimeException {

    /**
     * The singular constructor. Maps to RuntimeException's similar constructor.
     * @param inner The inner json exception to pass on.
     */
    public JsonParsingException(final JSONException inner) {
        super(inner);
    }
}
