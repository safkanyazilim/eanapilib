/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.util.Calendar;

public final class DateModifier {

    /**
     * Private, no-op constructor to prevent utility class instantiation.
     */
    private DateModifier() {
        // see javadoc
    }

    /**
     * Gets an array of Calendar objects in the same order and the same offsets from today as specified in offsets.
     * In this case an offset is a number of days (positive or negative) from today.
     * @param offsets The days offset from today for which to make calendar objects.
     * @return The Calendar objects which represent the days offset from today as specified by the passed offsets.
     */
    public static Calendar[] getAnArrayOfCalendarsWithOffsets(final int... offsets) {
        Calendar[] calendars = new Calendar[offsets.length];
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = Calendar.getInstance();
            calendars[i].add(Calendar.DAY_OF_YEAR, offsets[i]);
        }
        return calendars;
    }
}
