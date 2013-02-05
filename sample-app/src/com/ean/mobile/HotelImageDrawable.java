package com.ean.mobile;

import android.graphics.drawable.Drawable;

import java.io.IOException;

import com.ean.mobile.hotel.HotelImageTuple;

public final class HotelImageDrawable {

    private HotelImageTuple tuple;

    public HotelImageDrawable(final HotelImageTuple tuple) {
        this.tuple = tuple;
    }

    /**
     * The thumbnail resource for this image.
     */
    private Drawable thumbnail;

    /**
     * The main drawable resource for the main image .
     */
    private Drawable main;

    /**
     * Shows without loading the image whether or not the thumbnail image has been loaded from the remote url.
     * @return Whether or not the thumbnail has been loaded.
     */
    public boolean isThumbnailLoaded() {
        return thumbnail != null;
    }

    /**
     * Gets the thumbnail image as a drawable. If the image hasn't been loaded, will return null.
     * To load the image, use {@link com.ean.mobile.HotelImageDrawable#loadThumbnailImage()}.
     * @return The thumbnail image as a (possibly null) Drawable object.
     */
    public Drawable getThumbnailImage() {
        return thumbnail;
    }

    /**
     * Loads the thumbnail image from the thumbnail URL. If already loaded, will simply return that which
     * has been loaded.
     *
     * Unless {@link com.ean.mobile.HotelImageDrawable#isThumbnailLoaded()} returns true,
     * THIS SHOULD NOT BE RUN ON THE MAIN UI THREAD!! Use concurrency mechanisms such as AsyncTask to call this method.
     * @return An image that can be drawn to the screen using the android SDK
     * @throws java.io.IOException If there is an exception when loading the image.
     */
    public Drawable loadThumbnailImage() throws IOException {
        if (thumbnail == null && tuple.thumbnailUrl != null) {
            thumbnail = Drawable.createFromStream(ImageFetcher.fetch(tuple.thumbnailUrl), "src");
        }
        return thumbnail;
    }

    /**
     * Shows without loading the image whether or not the main image has been loaded from the remote url.
     * @return Whether or not the main has been loaded.
     */
    public boolean isMainImageLoaded() {
        return main != null;
    }

    /**
     * Gets the main image as a drawable. If the image hasn't been loaded, will return null.
     * To load the image, use {@link com.ean.mobile.HotelImageDrawable#loadMainImage()}.
     * @return The main image as a (possibly null) Drawable object.
     */
    public Drawable getMainImage() {
        return main;
    }


    /**
     * Loads the main image from the main URL. If already loaded, will simply return that which
     * has been loaded.
     *
     * Unless {@link HotelImageDrawable#isMainImageLoaded()} returns true,
     * THIS SHOULD NOT BE RUN ON THE MAIN UI THREAD!! Use concurrency mechanisms such as AsyncTask to call this method.
     * @return An image that can be drawn to the screen using the android SDK
     * @throws IOException If there is an exception when loading the image.
     */
    public Drawable loadMainImage() throws IOException {
        if (main == null && tuple.mainUrl != null) {
            main = Drawable.createFromStream(ImageFetcher.fetch(tuple.mainUrl), "src");
        }
        return main;
    }
}
