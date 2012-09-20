package com.ean.mobile.request;

import android.util.Log;
import com.ean.mobile.EANMobileConstants;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public abstract class Request {
    protected static final String CID = "55505";
    protected static final String MINOR_REV = "10";
    protected static final String API_KEY = "cbrzfta369qwyrm9t5b8y8kf";
    protected static final String LOCALE = "en_US";
    protected static final String CURRENCY_CODE = "USD";
    protected static final String URL_PROTOCOL = "http";
    //protected static final String URL_HOSTNAME = "stg1-www.travelnow.com";
    //protected static final String URL_HOSTNAME = "stg5-www.travelnow.com";
    //protected static final String URL_HOSTNAME = "xml.travelnow.com";
    protected static final String URL_HOSTNAME = "mobile.eancdn.com";
    protected static final String URL_BASEDIR = "/ean-services/rs/hotel/v3/";
    protected static final String DATE_FORMAT_STRING = "MM/dd/yyyy";

    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    protected static URL FULL_URL;

    static{
        try {
            FULL_URL = new URL(URL_PROTOCOL, URL_HOSTNAME, URL_BASEDIR);
        } catch(MalformedURLException mue) {
            Log.d(EANMobileConstants.DEBUG_TAG, "Base url is malformed");
        }
    }

    protected static JSONObject getJsonFromSubdir(String urlSubdir, String[][] params) throws IOException, JSONException {
        return getJsonFromSubdir(urlSubdir, getParams(params));
    }

    /**
     * This uses an ArrayList<NameValuePair> (rather than list) because that is what URLEncodedUtils specifies
     * @param urlSubdir
     * @param params
     * @return
     * @throws IOException
     * @throws JSONException
     */
    protected static JSONObject getJsonFromSubdir(String urlSubdir, ArrayList<NameValuePair> params) throws IOException, JSONException {
        //Build the url
        String baseUrl = new URL(FULL_URL, urlSubdir).toString();
        if(params != null && params.size() > 0){
            baseUrl += "?" + URLEncodedUtils.format(params, "UTF-8");
        }
        HttpGet getRequest = new HttpGet(baseUrl);
        getRequest.setHeader("Accept", "application/json, */*");
        Log.d(EANMobileConstants.DEBUG_TAG, "url: " + baseUrl);
        Log.d(EANMobileConstants.DEBUG_TAG, "getting response");
        long startTime = System.currentTimeMillis();
        HttpResponse response = new DefaultHttpClient().execute(getRequest);
        Log.d(EANMobileConstants.DEBUG_TAG,"got response");
        StatusLine statusLine = response.getStatusLine();
        JSONObject json;
        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            String jsonstr = out.toString();
            //Log.d(EANMobileConstants.DEBUG_TAG, jsonstr);
            json = new JSONObject(jsonstr);
        } else{
           // Closes the connection.
            response.getEntity().getContent().close();
            throw new IOException(statusLine.getReasonPhrase());
        }
        long timeTook = System.currentTimeMillis() - startTime;
        Log.d(EANMobileConstants.DEBUG_TAG, "Took " + timeTook + " milliseconds." );
        return json;
    }

    protected static ArrayList<NameValuePair> getParams(String[][] paramsArray) {
        ArrayList<NameValuePair> urlPairs = new ArrayList<NameValuePair>();
        for(String[] param : paramsArray){
            urlPairs.add(new BasicNameValuePair(param[0], param[1]));
        }
        return urlPairs;
    }


    
}