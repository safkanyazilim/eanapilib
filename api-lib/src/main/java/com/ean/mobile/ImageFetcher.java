/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * Uses apache's HttpClient to fetch the binary information for images.
 */
public final class ImageFetcher {

    /**
     * The protocol with which to fetch images.
     */
    private static final String IMAGE_PROTOCOL = "http";

    /**
     * The host from which to fetch images.
     */
    private static final String IMAGE_HOST = "images.travelnow.com";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private ImageFetcher() {
        // see javadoc
    }

    /**
     * Gets an input stream from the urlString. Throws an exception if the URL is invalid or
     * some other http exception has happened.
     * @param urlString The String representation of the url of the image.
     * @param fullURL Whether or not {#urlString} is the full url or just the path portion (minus host/protocol)
     * @return The input stream pointing to the image requested.
     * @throws IOException If an error occurred when connecting or transmitting the data from urlString.
     */
    public static InputStream fetch(final String urlString, final boolean fullURL) throws IOException {
        Log.d(Constants.DEBUG_TAG, urlString);
        final String finalUrl;
        if (!fullURL) {
            finalUrl = new URL(IMAGE_PROTOCOL, IMAGE_HOST, urlString).toString();
            Log.d(Constants.DEBUG_TAG, urlString);
        } else {
            finalUrl = urlString;
        }
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpGet request = new HttpGet(finalUrl);
        final HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    /**
     * Gets an input stream from the urlString. Throws an exception if the URL is invalid or
     * some other http exception has happened.
     * @param urlString The String representation of the url of the image. Must be fully qualified.
     * @return The input stream pointing to the image requested.
     * @throws IOException If an error occurred when connecting or transmitting the data from urlString.
     */
    public static InputStream fetch(final String urlString) throws IOException {
        return fetch(urlString, true);
    }

    /**
     * Gets an input stream from the url. Throws an exception if the URL is invalid or
     * some other http exception has happened.
     * @param url The url of the image. Must be fully qualified.
     * @return The input stream pointing to the image requested.
     * @throws IOException If an error occurred when connecting or transmitting the data from urlString.
     */
    public static InputStream fetch(final URL url) throws IOException {
        return fetch(url.toString(), true);
    }

    /**
     * Gets the full image url, based simply on the partial url which does not include the protocol or host.
     * @param partial The partial url, excluding the host and previous
     * @return The full url to the image.
     * @throws MalformedURLException If the default IMAGE_PROTOCOL and IMAGE_HOST, combined with partial do not
     *  create a valid URL.
     */
    public static URL getFullImageUrl(final String partial) throws MalformedURLException {
        return new URL(IMAGE_PROTOCOL, IMAGE_HOST, partial);
    }
}
