package com.ean.mobile.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import com.ean.mobile.Destination;
import com.ean.mobile.request.DestLookup;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://developer.android.com/guide/topics/providers/content-provider-creating.html
 */
public final class DestinationProvider extends ContentProvider {

    public static final URI AUTHORITY = URI.create("com.ean.mobile.provider");

    private Map<String, List<Destination>> destinationCache;

    private static final String[] COLUMN_NAMES = {"_ID", "category_localized", "category", "name"};

    private static final UriMatcher MATCHER;

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY.getHost(), "*", 0);
    }

    @Override
    public boolean onCreate() {
        destinationCache = new HashMap<String, List<Destination>>();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String query = "";
        if (!destinationCache.containsKey(query)) {
            List<Destination> destinations;
            try {
                destinations = DestLookup.getDestInfos(query);
            } catch (IOException ioe) {
                destinations = Collections.emptyList();
            }
            destinationCache.put(query, destinations);
        }
        MatrixCursor cursor = new MatrixCursor(COLUMN_NAMES);
        MatrixCursor.RowBuilder builder;
        for (Destination destination : destinationCache.get(query)) {
            builder = cursor.newRow();
            builder.add(destination.id);
            builder.add(destination.categoryLocalized);
            builder.add(destination.category);
            builder.add(destination.name);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
