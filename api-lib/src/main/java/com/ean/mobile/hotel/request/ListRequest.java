/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel.request;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.hotel.Hotel;
import com.ean.mobile.hotel.HotelList;
import com.ean.mobile.hotel.RoomOccupancy;
import com.ean.mobile.request.Request;

/**
 * The most useful method gets the List of hotels based on the search parameters, particularly the destination passed.
 */
public final class ListRequest extends Request<HotelList> {

    private static final String NUMBER_OF_RESULTS = "10";

    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * @param destination The destination to search for hotel availability.
     * @param occupancy The stated occupancy to search for.
     * @param arrivalDate The arrival date of the request.
     * @param departureDate The departure date of the request.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *     The same customerSessionId as returned in other API requests
     * @param locale The locale in which to perform the request.
     * @param currencyCode The currency which is desired in the response.
     */
    public ListRequest(final String destination, final RoomOccupancy occupancy,
            final LocalDate arrivalDate, final LocalDate departureDate,
            final String customerSessionId, final String locale, final String currencyCode) {

        this(destination, Collections.singletonList(occupancy), arrivalDate, departureDate,
                customerSessionId, locale, currencyCode);
    }
    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * @param destination The destination to search for hotel availability.
     * @param occupancies The stated occupancy of each room to search for.
     * @param arrivalDate The arrival date of the request.
     * @param departureDate The departure date of the request.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *     The same customerSessionId as returned in other API requests
     * @param locale The locale to search for the hotels in.
     * @param currencyCode The currency code to search for.
     *                     Can be any valid currency, but can only book chargeable currencies.
     */
    public ListRequest(final String destination, final List<RoomOccupancy> occupancies,
            final LocalDate arrivalDate, final LocalDate departureDate,
            final String customerSessionId, final String locale, final String currencyCode) {

        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("destinationString", destination),
            new BasicNameValuePair("numberOfResults", NUMBER_OF_RESULTS)
        );

        final List<NameValuePair> roomParameters = new ArrayList<NameValuePair>(occupancies.size());

        int roomNumber = 1;
        for (RoomOccupancy occupancy : occupancies) {
            roomParameters.add(new BasicNameValuePair("room" + roomNumber, occupancy.asAbbreviatedRequestString()));
            roomNumber++;
        }

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters(locale, currencyCode, arrivalDate, departureDate));
        urlParameters.addAll(requestParameters);
        if (customerSessionId != null) {
            urlParameters.add(new BasicNameValuePair("customerSessionId", customerSessionId));
        }
        urlParameters.addAll(roomParameters);

        setUrlParameters(urlParameters);
    }

    /**
     * Loads more results into a HotelList so pagination can be supported.
     * @param locale Locale
     * @param currencyCode Currency Code
     * @param cacheKey Cache key from previous request
     * @param cacheLocation Cache location from previous request
     * @param customerSessionId Customer Session Id obtained from previous requests, pass
     *      in to track as the user moves around requests and booking flow.
     */
    public ListRequest(final String locale, final String currencyCode,
                       final String cacheKey, final String cacheLocation,
                       final String customerSessionId) {

        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("cacheKey", cacheKey),
                new BasicNameValuePair("cacheLocation", cacheLocation),
                new BasicNameValuePair("customerSessionId", customerSessionId)
        );

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters(locale, currencyCode));
        urlParameters.addAll(requestParameters);

        setUrlParameters(urlParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HotelList consume(final JSONObject jsonObject) throws JSONException, EanWsError {
        if (jsonObject == null) {
            return null;
        }

        final JSONObject response = jsonObject.getJSONObject("HotelListResponse");

        if (response.has("EanWsError")) {
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }

        final String newCacheKey = response.optString("cacheKey");
        final String newCacheLocation = response.optString("cacheLocation");
        final String outgoingCustomerSessionId = response.optString("customerSessionId");
        final int totalNumberOfResults = response.optJSONObject("HotelList").optInt("@activePropertyCount");

        final JSONArray newHotelJson = response.getJSONObject("HotelList").getJSONArray("HotelSummary");
        final List<Hotel> newHotels = new ArrayList<Hotel>(newHotelJson.length());
        for (int i = 0; i < newHotelJson.length(); i++) {
            try {
                newHotels.add(new Hotel(newHotelJson.getJSONObject(i)));
            } catch (MalformedURLException me) {
                Log.e("Unable to process JSON", me.getMessage());
            }
        }

        return new HotelList(newHotels,
            newCacheKey, newCacheLocation, outgoingCustomerSessionId, totalNumberOfResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUri() throws URISyntaxException {
        return new URI("http", "api.ean.com", "/ean-services/rs/hotel/v3/list", getQueryString(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresSecure() {
        return false;
    }

}
