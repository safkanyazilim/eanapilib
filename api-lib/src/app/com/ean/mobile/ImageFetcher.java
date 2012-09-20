package com.ean.mobile;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageFetcher {

    private static final String IMAGE_PROTOCOL = "http",
                               IMAGE_HOST = "images.travelnow.com";

    public static InputStream fetch(String urlString, boolean fullURL) throws IOException {
        Log.d(EANMobileConstants.DEBUG_TAG, urlString);
        if(!fullURL) {
            urlString = new URL(IMAGE_PROTOCOL, IMAGE_HOST, urlString).toString();
            Log.d(EANMobileConstants.DEBUG_TAG, urlString);
        }
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    public static InputStream fetch(String urlString) throws IOException {
        return fetch(urlString, false);
    }
}
