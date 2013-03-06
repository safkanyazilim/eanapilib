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

package com.ean.mobile.app;

import java.io.IOException;

import android.graphics.drawable.Drawable;

import com.ean.mobile.hotel.HotelImageTuple;

/**
 * Holder of the actual {@link Drawable} objects found at the urls specified by a HotelImageTuple. Also contains
 * code to actually fetch the images using the {@link ImageFetcher}.
 */
public final class HotelImageDrawable {

    private HotelImageTuple tuple;

    /**
     * The thumbnail resource for this image.
     */
    private Drawable thumbnail;

    /**
     * The main drawable resource for the main image .
     */
    private Drawable main;

    /**
     * Constructs this object with the associated {@link HotelImageTuple} so we have the urls to download the
     * appropriate images.
     * @param tuple The object used for the source of the {@link Drawable} objects in this class.
     */
    public HotelImageDrawable(final HotelImageTuple tuple) {
        this.tuple = tuple;
    }

    /**
     * Shows without loading the image whether or not the thumbnail image has been loaded from the remote url.
     * @return Whether or not the thumbnail has been loaded.
     */
    public boolean isThumbnailLoaded() {
        return thumbnail != null;
    }

    /**
     * Gets the thumbnail image as a drawable. If the image hasn't been loaded, will return null.
     * To load the image, use {@link HotelImageDrawable#loadThumbnailImage()}.
     * @return The thumbnail image as a (possibly null) Drawable object.
     */
    public Drawable getThumbnailImage() {
        return thumbnail;
    }

    /**
     * Loads the thumbnail image from the thumbnail URL. If already loaded, will simply return that which
     * has been loaded.
     *
     * Unless {@link HotelImageDrawable#isThumbnailLoaded()} returns true,
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
     * To load the image, use {@link HotelImageDrawable#loadMainImage()}.
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
