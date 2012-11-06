/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package android.util;

import org.apache.commons.logging.LogFactory;

/**
 * Stub of android.util.log. DO NOT USE IN PRODUCTION!!
 */
public final class Log {
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(Log.class);

    public static int d(final String tag, final String message) {
        LOG.debug(tag + " : " + message);
        return 0;
    }
}
