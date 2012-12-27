package com.ean.mobile.request.data;

/**
 * A class that holds the first and last name of an individual in an easy to use container.
 */
public final class Name {
    public final String first;

    public final String last;

    public Name(final String first, final String last) {
        this.first = first;
        this.last = last;
    }
}
