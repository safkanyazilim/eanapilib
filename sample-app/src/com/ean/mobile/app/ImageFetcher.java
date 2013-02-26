/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Build;
import android.widget.ImageView;

import com.ean.mobile.hotel.HotelImageTuple;
import com.ean.mobile.task.ImageDrawableLoaderTask;

/**
 * Uses methods described <a href="http://android-developers.blogspot.com/2011/09/androids-http-clients.html">here</a>
 * to fetch the binary information for images.
 */
public final class ImageFetcher {

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private ImageFetcher() {
        // see javadoc
    }

    /**
     * Gets an input stream from the urlString. Throws an exception if the URL is invalid or
     * some other http exception has happened.
     * @param url The url of the image.
     * @param fullURL Whether or not {#urlString} is the full url or just the path portion (minus host/protocol)
     * @return The input stream pointing to the image requested.
     * @throws IOException If an error occurred when connecting or transmitting the data from urlString.
     */
    public static InputStream fetch(final URL url, final boolean fullURL) throws IOException {
        if (url == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpGet request = new HttpGet(url.toString());
            final HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        } else {
            final URLConnection connection = url.openConnection();
            return connection.getInputStream();

        }
    }

    /**
     * Gets an input stream from the url. Throws an exception if the URL is invalid or
     * some other http exception has happened.
     * @param url The url of the image. Must be fully qualified.
     * @return The input stream pointing to the image requested.
     * @throws IOException If an error occurred when connecting or transmitting the data from urlString.
     */
    public static InputStream fetch(final URL url) throws IOException {
        return fetch(url, true);
    }

    /**
     * Loads a thumbnail image from a {@link HotelImageDrawable} into the passed ImageView automatically.
     * This method is safe to run on the main thread since it will automatically start an {@link android.os.AsyncTask}
     * ({@link ImageDrawableLoaderTask} to be exact) to load the image if it hasn't already been downloaded.
     * @param thumb The drawable into which to load the image.
     * @param tuple The tuple holding the thumbnail intended to be loaded into thumb.
     */
    public static void loadThumbnailIntoImageView(final ImageView thumb, final HotelImageTuple tuple) {
        final HotelImageDrawable hotelImageDrawable = SampleApp.IMAGE_DRAWABLES.get(tuple);
        if (tuple.thumbnailUrl != null) {
            if (hotelImageDrawable.isThumbnailLoaded()) {
                thumb.setImageDrawable(hotelImageDrawable.getThumbnailImage());
            } else {
                new ImageDrawableLoaderTask(thumb, false).execute(hotelImageDrawable);
            }
        }
    }

}
