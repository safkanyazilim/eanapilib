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
import com.ean.mobile.hotel.Cancellation;
import com.ean.mobile.request.Request;

/**
 * Used to request hotel room cancellations through the EAN API.
 */
public class CancellationRequest extends Request<Cancellation> {

    /**
     * Uses the EAN API to request a cancellation for a previous booking.
     *
     * @param itineraryId the ID of the itinerary that should be cancelled.
     * @param confirmationNumber the confirmation number associated with the booking.
     * @param emailAddress the e-mail address associated with the itinerary.
     * @param reason a reason for the cancellation.
     */
    public CancellationRequest(
            final long itineraryId, final long confirmationNumber, final String emailAddress, final String reason) {
        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("itineraryId", String.valueOf(itineraryId)),
            new BasicNameValuePair("confirmationNumber", String.valueOf(confirmationNumber)),
            new BasicNameValuePair("email", emailAddress),
            new BasicNameValuePair("reason", reason)
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
    public Cancellation consume(final JSONObject jsonObject) throws JSONException, EanWsError {
        if (jsonObject == null) {
            return null;
        }

        final JSONObject response = jsonObject.getJSONObject("HotelRoomCancellationResponse");
        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }

        return new Cancellation(response.getString("cancellationNumber"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUri() throws URISyntaxException {
        return new URI("http", "api.ean.com", "/ean-services/rs/hotel/v3/cancel", getQueryString(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresSecure() {
        return false;
    }
}
