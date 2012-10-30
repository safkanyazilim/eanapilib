/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.io.IOException;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

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
     * Calls setImageDrawable on the passed ImageView with the thumbnail resource.
     * @param thumbnailView The view to which to set the drawable.
     */
    public void setImageViewToThumbnail(final ImageView thumbnailView) {
        if (thumbnail == null) {
            new ThumbnailImageLoadTask(thumbnailView).execute(thumbnailUrl);
        } else {
            thumbnailView.setImageDrawable(thumbnail);
        }
    }

    /**
     * Calls setImageDrawable on the passed ImageView with the main image resource.
     * @param mainView The view to which to set the drawable.
     */
    public void setImageViewToMain(final ImageView mainView) {
        if (main == null) {
            new MainImageLoadTask(mainView).execute(mainUrl);
        } else {
            mainView.setImageDrawable(main);
        }
    }

    /**
     * Base class for the different image loading tasks.
     */
    private abstract class ImageLoadTask extends AsyncTask<URL, Void, Drawable> {

        /**
         * The view onto which to draw the retrieved image.
         */
        private final ImageView viewToDraw;

        /**
         * Sets up the view onto which to draw.
         * @param view The view to set to v
         */
        protected ImageLoadTask(final ImageView view) {
            this.viewToDraw = view;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Drawable doInBackground(final URL... urls) {
            Drawable image = null;
            try {
                image = Drawable.createFromStream(ImageFetcher.fetch(urls[0].toString(), true), "src");
            } catch (IOException e) {
                Log.d(EANMobileConstants.DEBUG_TAG, "Exception occurred while retrieving thumbnail" + e.getMessage());
            }
            return image;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected final void onPostExecute(final Drawable result) {
            viewToDraw.setImageDrawable(result);
            onPostExecuteSpecific(result);
        }

        /**
         * The implementation analogous to onPostExecute, but with the ability to not need to know about super.
         * @param result The result passed to onPostExecute
         */
        protected abstract void onPostExecuteSpecific(Drawable result);
    }

    /**
     * The concrete implementation for loading the thumbnails.
     */
    private class ThumbnailImageLoadTask extends ImageLoadTask {
        /**
         * The necessary constructor since the default constructor is not available.
         * @param view The view to place the thumbnail into.
         */
        protected ThumbnailImageLoadTask(final ImageView view) {
            super(view);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecuteSpecific(final Drawable result) {
            thumbnail = result;
        }
    }

    /**
     * The concrete implementation for loading the main images.
     */
    private class MainImageLoadTask extends ImageLoadTask {

        /**
         * The necessary constructor since the default constructor is not available.
         * @param view The view to place the main image into.
         */
        protected MainImageLoadTask(final ImageView view) {
            super(view);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecuteSpecific(final Drawable result) {
            main = result;
        }
    }
}
