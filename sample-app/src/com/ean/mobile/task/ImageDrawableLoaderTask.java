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