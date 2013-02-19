/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.task;

import java.io.IOException;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.ean.mobile.app.HotelImageDrawable;
import com.ean.mobile.app.SampleConstants;

/**
 * A task that will load one of the images represented in a {@link HotelImageDrawable} (as specified by loadMain)
 * into the passed {@link ImageView}.
 */
public final class ImageDrawableLoaderTask extends AsyncTask<HotelImageDrawable, Integer, Drawable> {

    private final ImageView view;

    private final boolean loadMain;

    /**
     * Determines which image to load and into what ImageView.
     * @param view The view to load the image into.
     * @param loadMain True to load {@link HotelImageDrawable#main}, false to load {@link HotelImageDrawable#thumbnail}.
     */
    public ImageDrawableLoaderTask(final ImageView view, final boolean loadMain) {
        super();
        this.view = view;
        this.loadMain = loadMain;
    }

    /**
     * Assumes that hotelImageTuples is of size 1, and loads the appropriate image from the network connection.
     * {@inheritDoc}
     */
    @Override
    protected Drawable doInBackground(final HotelImageDrawable... hotelImageTuples) {
        try {
            return loadMain ? hotelImageTuples[0].loadMainImage() : hotelImageTuples[0].loadThumbnailImage();
        } catch (IOException ioe) {
            Log.d(SampleConstants.LOG_TAG, "An error occurred when loading hotel's main thumbnail", ioe);
        }
        return null;
    }

    /**
     * Sets the drawable image in the {@link ImageView} passed during construction to that loaded during
     * {@link ImageDrawableLoaderTask#doInBackground(com.ean.mobile.app.HotelImageDrawable...)}.
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(final Drawable drawable) {
        view.setImageDrawable(drawable);
    }

}