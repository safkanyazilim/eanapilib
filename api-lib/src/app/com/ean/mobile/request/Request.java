/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import com.ean.mobile.Constants;

public abstract class Request {
    protected static final String CID = "55505";
    protected static final String MINOR_REV = "10";
    protected static final String API_KEY = "cbrzfta369qwyrm9t5b8y8kf";
    protected static final String LOCALE = "it_IT";
    protected static final String CURRENCY_CODE = "USD";
    protected static final String URL_PROTOCOL = "http";
    //protected static final String URL_HOSTNAME = "stg1-www.travelnow.com";
    //protected static final String URL_HOSTNAME = "stg5-www.travelnow.com";
    //protected static final String URL_HOSTNAME = "xml.travelnow.com";
    protected static final String URL_HOSTNAME = "mobile.eancdn.com";
    protected static final String URL_BASEDIR = "/ean-services/rs/hotel/v3/";
    protected static final String DATE_FORMAT_STRING = "MM/dd/yyyy";

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    protected static final URL FULL_URL;

    static {
        URL fullUrl = null;
        try {
            fullUrl = new URL(URL_PROTOCOL, URL_HOSTNAME, URL_BASEDIR);
        } catch (MalformedURLException mue) {
            Log.d(Constants.DEBUG_TAG, "Base url is malformed");
        }
        FULL_URL = fullUrl;
    }

    protected static JSONObject getJsonFromSubdir(final String urlSubdir, final String[][] params)
        throws IOException, JSONException {
        return getJsonFromSubdir(urlSubdir, getParams(params));
    }

    protected static JSONObject getJsonFromSubdir(final String urlSubdir, final List<NameValuePair> params)
            throws IOException, JSONException {
        //TODO: This URL building is inefficient and somewhat incorrect.
        //Build the url
        final String baseUrl = new URL(FULL_URL, urlSubdir).toString();
        final HttpGet getRequest = new HttpGet(createFullUrl(baseUrl, params).toString());
        getRequest.setHeader("Accept", "application/json, */*");
        Log.d(Constants.DEBUG_TAG, "url: " + baseUrl);
        Log.d(Constants.DEBUG_TAG, "getting response");
        final long startTime = System.currentTimeMillis();
        final HttpResponse response = new DefaultHttpClient().execute(getRequest);
        Log.d(Constants.DEBUG_TAG, "got response");
        final StatusLine statusLine = response.getStatusLine();
        final JSONObject json;
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            final String jsonstr = out.toString();
            //Log.d(Constants.DEBUG_TAG, jsonstr);
            json = new JSONObject(jsonstr);
        } else {
            // Closes the connection.
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }
        final long timeTook = System.currentTimeMillis() - startTime;
        Log.d(Constants.DEBUG_TAG, "Took " + timeTook + " milliseconds.");
        return json;
    }

    protected static List<NameValuePair> getParams(final String[][] paramsArray) {
        final List<NameValuePair> urlPairs = new ArrayList<NameValuePair>(paramsArray.length);
        for (String[] param : paramsArray) {
            urlPairs.add(new BasicNameValuePair(param[0], param[1]));
        }
        return urlPairs;
    }

    protected static URL createFullUrl(final String baseUrl, final List<NameValuePair> params)
            throws MalformedURLException {
        String queryString = "";
        if (params != null && !params.isEmpty()) {
            queryString = "?" + URLEncodedUtils.format(params, "UTF-8");
        }

        return new URL(baseUrl + queryString);
    }
}