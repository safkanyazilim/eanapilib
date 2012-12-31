package com.ean.mobile;

import org.json.JSONObject;

/**
 * A class that holds the first and last name of an individual in an easy to use container.
 */
public final class Name {

    /**
     * The first name of the person represented.
     */
    public final String first;

    /**
     * The last name of the person represented.
     */
    public final String last;

    /**
     * The constructor for this name object.
     * @param first The first name to set.
     * @param last The last name.
     */
    public Name(final String first, final String last) {
        this.first = first;
        this.last = last;
    }

    public Name(final JSONObject object) {
        this.first = object.optString("firstName");
        this.last = object.optString("lastName");
    }
}