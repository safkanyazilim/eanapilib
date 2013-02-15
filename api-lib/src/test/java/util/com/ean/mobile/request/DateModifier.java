/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateModifier {

    /**
     * Private, no-op constructor to prevent utility class instantiation.
     */
    private DateModifier() {
        // see javadoc
    }

    /**
     * Gets an array of LocalDate objects in the same order and the same offsets from today as specified in offsets.
     * In this case an offset is a number of days (positive or negative) from today.
     * @param offsets The days offset from today for which to make DateTime objects.
     * @return The DateTime objects which represent the days offset from today as specified by the passed offsets.
     */
    public static LocalDate[] getAnArrayOfLocalDatesWithOffsets(final int... offsets) {
        LocalDate[] calendars = new LocalDate[offsets.length];
        for (int i = 0; i < calendars.length; i++) {
            calendars[i] = LocalDate.now().plusDays(offsets[i]);
        }
        return calendars;
    }

    /**
     * Returns a LocalDate that matches the given dateString.
     * @param dateString a String representation of the desired date. MUST be in the format MM/DD/yyyy.
     * @return a LocalDate object representing the desired date.
     */
    public static LocalDate getDateFromString(final String dateString) {
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");
        return dtf.parseLocalDate(dateString);
    }
}
