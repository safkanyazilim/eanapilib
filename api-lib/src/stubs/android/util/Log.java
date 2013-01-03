/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
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

    public static int e(final String tag, final String message) {
        LOG.error(tag + " : " + message);
        return 0;
    }

    public static int wtf(final String tag, final String message) {
        LOG.fatal(tag + " : " + message);
        return 0;
    }
}
