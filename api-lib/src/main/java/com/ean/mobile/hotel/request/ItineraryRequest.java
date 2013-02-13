/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.Itinerary;
import com.ean.mobile.request.CommonParameters;
import com.ean.mobile.request.Request;

/**
 * Used to retrieve and parse itinerary information from the EAN API.
 */
public class ItineraryRequest extends Request<Itinerary> {

    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * @param itineraryId the ID of the itinerary to retrieve.
     * @param emailAddress the e-mail address associated with the itinerary.
     */
    public ItineraryRequest(final long itineraryId, final String emailAddress) {
        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("itineraryId", String.valueOf(itineraryId)),
            new BasicNameValuePair("email", emailAddress)
        );

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters());
        urlParameters.addAll(requestParameters);
        setUrlParameters(urlParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Itinerary consume(final JSONObject jsonObject) throws JSONException, EanWsError {
        if (jsonObject == null) {
            return null;
        }

        final JSONObject response = jsonObject.getJSONObject("HotelItineraryResponse");
        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }

        CommonParameters.customerSessionId = response.optString("customerSessionId");

        return new Itinerary(response.getJSONObject("Itinerary"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUri() throws URISyntaxException {
        return new URI("http", "api.ean.com", "/ean-services/rs/hotel/v3/itin", getQueryString(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresSecure() {
        return false;
    }

}
