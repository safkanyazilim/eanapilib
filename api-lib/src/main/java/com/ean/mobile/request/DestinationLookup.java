/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.Destination;
import com.ean.mobile.exception.EanWsError;

/**
 * Looks up possible destinations based on the destinationString passed to the constructor.
 */
public final class DestinationLookup extends Request<List<Destination>> {

    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * @param destinationString The destination to search for hotels.
     */
    public DestinationLookup(final String destinationString) {
        if (destinationString != null) {
            final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("propertyName", destinationString));
            setUrlParameters(urlParameters);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Destination> consume(final JSONObject jsonObject) throws JSONException, EanWsError {
        return Destination.getDestinations(jsonObject.getJSONArray("items"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUri() throws URISyntaxException {
        return new URI("http", "www.travelnow.com", "/templates/349176/destination", getQueryString(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSecure() {
        return false;
    }

}
