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

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
 */

package com.ean.mobile;

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
            //TODO: This could do with some transparent gzipping maybe?
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
