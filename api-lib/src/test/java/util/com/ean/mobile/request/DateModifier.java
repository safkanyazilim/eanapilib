/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import org.joda.time.DateTime;

public final class DateModifier {

    /**
     * Private, no-op constructor to prevent utility class instantiation.
     */
    private DateModifier() {
        // see javadoc
    }

    /**
     * Gets an array of DateTime objects in the same order and the same offsets from today as specified in offsets.
     * In this case an offset is a number of days (positive or negative) from today.
     * @param offsets The days offset from today for which to make DateTime objects.
     * @return The DateTime objects which represent the days offset from today as specified by the passed offsets.
     */
    public static DateTime[] getAnArrayOfDateTimesWithOffsets(final int... offsets) {
        DateTime[] calendars = new DateTime[offsets.length];
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = DateTime.now().plusDays(offsets[i]);
        }
        return calendars;
    }
}
