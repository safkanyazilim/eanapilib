/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
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
