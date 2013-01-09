/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;
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

import com.ean.mobile.HotelInfo;
import com.ean.mobile.HotelInfoList;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.EanWsError;

/**
 * The most useful method gets the List of hotels based on the search parameters, particularly the destination passed.
 */
public final class ListRequest extends Request {

    private static final String NUMBER_OF_RESULTS = "10";
    private static final String URL_SUBDIR = "list";

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private ListRequest() {
        //see javadoc.
    }

    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * THIS SHOULD NOT BE RUN ON THE MAIN THREAD. It is a long-running network process and so might cause
     * force close dialogs.
     * @param destination The destination to search for hotel availability.
     * @param occupancy The stated occupancy to search for.
     * @param arrivalDate The arrival date of the request.
     * @param departureDate The departure date of the request.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *     The same customerSessionId as returned in other API requests
     * @param locale The locale in which to perform the request.
     * @param currencyCode The currency which is desired in the response.
     * @return The list of HotelInfo that were requested by the search parameters.
     * @throws IOException If there was a network-level error.
     * @throws EanWsError If the API encountered an error and was unable to return results.
     */
    public static HotelInfoList searchForHotels(final String destination,
                                                final RoomOccupancy occupancy,
                                                final LocalDate arrivalDate,
                                                final LocalDate departureDate,
                                                final String customerSessionId,
                                                final String locale,
                                                final String currencyCode) throws IOException, EanWsError {
        return searchForHotels(
                destination,
                Collections.singletonList(occupancy),
                arrivalDate,
                departureDate,
                customerSessionId,
                locale,
                currencyCode);
    }
    /**
     * Uses the EAN API to search for hotels in the given destination using http requests.
     *
     * THIS SHOULD NOT BE RUN ON THE MAIN THREAD. It is a long-running network process and so might cause
     * force close dialogs.
     * @param destination The destination to search for hotel availability.
     * @param occupancies The stated occupancy of each room to search for.
     * @param arrivalDate The arrival date of the request.
     * @param departureDate The departure date of the request.
     * @param customerSessionId The session id of this customer, used to help speed requests on the API side.
     *     The same customerSessionId as returned in other API requests
     * @param locale The locale to search for the hotels in.
     * @param currencyCode The currency code to search for.
     *                     Can be any valid currency, but can only book chargeable currencies.
     * @return The list of HotelInfo that were requested by the search parameters.
     * @throws IOException If there was a network-level error.
     * @throws EanWsError If the API encountered an error and was unable to return results.
     */
    public static HotelInfoList searchForHotels(final String destination,
                                                final List<RoomOccupancy> occupancies,
                                                final LocalDate arrivalDate,
                                                final LocalDate departureDate,
                                                final String customerSessionId,
                                                final String locale,
                                                final String currencyCode) throws IOException, EanWsError {

        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("destinationString", destination),
                new BasicNameValuePair("numberOfResults", NUMBER_OF_RESULTS),
                new BasicNameValuePair("customerSessionId", customerSessionId)
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
        urlParameters.addAll(roomParameters);

        // TODO: Possibly cache the HotelInfoLists (factory?) such that if the request is performed again
        // within a certain threshold (maybe a day?) the search appears to be instantaneous.
        // This has the potential to be a memory hog given that the HotelImageTuples store the actual
        // bytes of the images they represent, once loaded.
        // TODO: Support pagination via cachekey and so forth
        try {
            final JSONObject listResponse
                    = performApiRequest(URL_SUBDIR, urlParameters).optJSONObject("HotelListResponse");

            if (listResponse.has("EanWsError")) {
                throw EanWsError.fromJson(listResponse.getJSONObject("EanWsError"));
            }

            final String cacheKey = listResponse.optString("cacheKey");
            final String cacheLocation = listResponse.optString("cacheLocation");
            final String outgoingCustomerSessionId = listResponse.optString("customerSessionId");
            final int pageSize = listResponse.optJSONObject("HotelList").optInt("@size");
            final int totalNumberOfResults = listResponse.optJSONObject("HotelList").optInt("@activePropertyCount");
            final JSONArray hotelList = listResponse
                .getJSONObject("HotelList")
                .getJSONArray("HotelSummary");
            final List<HotelInfo> hotels = new ArrayList<HotelInfo>(hotelList.length());
            for (int i = 0; i < hotelList.length(); i++) {
                hotels.add(new HotelInfo(hotelList.getJSONObject(i), i));
            }

            return new HotelInfoList(
                    hotels,
                    cacheKey,
                    cacheLocation,
                    outgoingCustomerSessionId,
                    pageSize,
                    totalNumberOfResults,
                    locale,
                    currencyCode);
        } catch (JSONException jse) {
            return HotelInfoList.empty();
        }
    }

    /**
     * Loads more results into a HotelInfoList so pagination can be supported.
     * @param list The HotelInfoList that is being loaded.
     * @return The newly grown HotelInfoList
     * @throws IOException If there is a network connection issue.
     * @throws EanWsError If there is an error with the API request.
     */
    public static HotelInfoList loadMoreResults(final HotelInfoList list) throws IOException, EanWsError {
        final int myPageIndex = list.allocateNewPageIndex();
        final int myStartIndex = list.pageSize * myPageIndex;
        final List<NameValuePair> requestParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("cacheKey", list.cacheKey),
                new BasicNameValuePair("cacheLocation", list.cacheLocation),
                new BasicNameValuePair("customerSessionId", list.customerSessionId)
        );

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters(list.locale, list.currencyCode));
        urlParameters.addAll(requestParameters);
        try {
            final JSONObject listResponse
                    = performApiRequest(URL_SUBDIR, urlParameters).optJSONObject("HotelListResponse");

            if (listResponse.has("EanWsError")) {
                throw EanWsError.fromJson(listResponse.getJSONObject("EanWsError"));
            }

            final JSONArray newHotelJson = listResponse
                    .getJSONObject("HotelList")
                    .getJSONArray("HotelSummary");
            final List<HotelInfo> newHotels = new ArrayList<HotelInfo>(newHotelJson.length());
            for (int i = 0; i < newHotelJson.length(); i++) {
                newHotels.add(new HotelInfo(newHotelJson.getJSONObject(i), i + myStartIndex));
            }

            list.addAll(myStartIndex, newHotels);
            return list;
        } catch (JSONException jse) {
            // we'll do nothing.
            return list;
        }
    }
}
