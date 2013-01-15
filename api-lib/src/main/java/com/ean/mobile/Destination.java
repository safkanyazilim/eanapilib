/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Holds the data returned from a destination lookup.
 */
public final class Destination {

    /**
     * The category that this destination represents.
     */
    public enum Category {
        /**
         * Notes that this destination is an airport.
         */
        AIRPORTS,

        /**
         * Notes that this destination is a city.
         */
        CITIES,

        /**
         * Notes that this destination is a hotel.
         */
        HOTELS,

        /**
         * Notes that this destination is a landmark.
         */
        LANDMARKS,

        /**
         * Notes that this destination is an unknown type. Likely means that the data source has
         * changed and the available set of categories have changed. This indicates that the api-lib
         * (specifically this class) needs to be updated.
         */
        UNKNOWN;

        /**
         * Safely parses a Category from a string, handling nulls and unknown categories by calling them UNKNOWN.
         * @param category The string to parse
         * @return The appropriate category.
         */
        public static Category parse(final String category) {
            if (category == null) {
                return UNKNOWN;
            }
            try {
                return Category.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return UNKNOWN;
            }
        }
    }

    /**
     * The destinationId of the destination, as returned by the instant-search endpoint. Does not correlate
     * directly to the standard EAN API's destinationId parameter.
     */
    public final String id;

    /**
     * The localized (human-readable) category. Can be used for displaying different categories of destinations.
     */
    public final String categoryLocalized;

    /**
     * The category of this destination.
     */
    public final Category category;

    /**
     * The human-readable name of this destination.
     */
    public final String name;

    /**
     * The sole JSON-based constructor for Destinations. The object must have fields
     * [id, categoryLocalized, category, name].
     * @param object The JSONObject with the aforementioned fields
     */
    public Destination(final JSONObject object) {
        this.id = object.optString("id");
        this.categoryLocalized = object.optString("categoryLocalized");
        this.category = Category.parse(object.optString("category"));
        this.name = object.optString("name");
    }

    /**
     * Gets a list of destinations from a JSONArray of destination objects holding data as described in the constructor.
     * @param destinations The json array.
     * @return The requested list.
     */
    public static List<Destination> getDestinations(final JSONArray destinations) {
        final List<Destination> localDestinations = new ArrayList<Destination>(destinations.length());
        for (int i = 0; i < destinations.length(); i++) {
            localDestinations.add(new Destination(destinations.optJSONObject(i)));
        }
        return Collections.unmodifiableList(localDestinations);
    }

    /**
     * Returns the name of this destination.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name + " (" + this.categoryLocalized + ")";
    }
}
