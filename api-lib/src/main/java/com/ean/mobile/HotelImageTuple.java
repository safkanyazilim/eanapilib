/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.io.IOException;
import java.net.URL;

import android.graphics.drawable.Drawable;
/**
 * This holds information about each of the images to be displayed in the hotel full information. This includes
 * inner views, exterior views and so forth.
 */
public final class HotelImageTuple {

    /**
     * The URL from whence to retrieve the thumbnail.
     */
    public final URL thumbnailUrl;

    /**
     * The URL from whence to retrieve the main image.
     */
    public final URL mainUrl;

    /**
     * The caption for the image.
     */
    public final String caption;

    /**
     * The thumbnail resource for this image.
     */
    private Drawable thumbnail;

    /**
     * The main drawable resource for the main image .
     */
    private Drawable main;

    /**
     * Constructs the object with final values.
     * @param thumbnailUrl The URL of the thumbnail image.
     * @param mainUrl The URL of the main image.
     * @param caption The caption for the image.
     */
    public HotelImageTuple(final URL thumbnailUrl, final URL mainUrl, final String caption) {
        this.thumbnailUrl = thumbnailUrl;
        this.mainUrl = mainUrl;
        this.caption = caption;
    }

    /**
     * Shows without loading the image whether or not the thumbnail image has been loaded from the remote url.
     * @return Whether or not the thumbnail has been loaded.
     */
    public boolean isThumbnailLoaded() {
        return thumbnail == null;
    }

    /**
     * Gets the thumbnail image from the thumbnail URL. If already loaded, will simply return that which
     * has been loaded.
     *
     * Unless {@link com.ean.mobile.HotelImageTuple#isThumbnailLoaded()} returns true,
     * THIS SHOULD NOT BE RUN ON THE MAIN UI THREAD!! Use concurrency mechanisms such as AsyncTask to call this method.
     * @return An image that can be drawn to the screen using the android SDK
     * @throws IOException If there is an exception when loading the image.
     */
    public Drawable getThumbnailImage() throws IOException {
        if (thumbnail == null) {
            thumbnail = Drawable.createFromStream(ImageFetcher.fetch(thumbnailUrl), "src");
        }
        return thumbnail;
    }

    /**
     * Shows without loading the image whether or not the main image has been loaded from the remote url.
     * @return Whether or not the main has been loaded.
     */
    public boolean isMainImageLoaded() {
        return main == null;
    }

    /**
     * Gets the main image from the main URL. If already loaded, will simply return that which
     * has been loaded.
     *
     * Unless {@link HotelImageTuple#isMainImageLoaded()} returns true,
     * THIS SHOULD NOT BE RUN ON THE MAIN UI THREAD!! Use concurrency mechanisms such as AsyncTask to call this method.
     * @return An image that can be drawn to the screen using the android SDK
     * @throws IOException If there is an exception when loading the image.
     */
    public Drawable getMainImage() throws IOException {
        if (main == null) {
            main = Drawable.createFromStream(ImageFetcher.fetch(mainUrl), "src");
        }
        return main;
    }

}
