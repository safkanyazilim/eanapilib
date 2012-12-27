/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.joda.time.DateTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ean.mobile.HotelRoom;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.JsonParsingException;
import com.ean.mobile.request.data.Name;
import com.ean.mobile.request.data.ReservationInfo;

/**
 * The class that performs booking requests.
 */
public final class BookingRequest extends Request {

    /**
     * This is the subdir that is used to make booking requests.
     */
    private static final String URL_SUBDIR = "res";

    /**
     * This method actually performs the booking request with the passed room and occupancy information.
     * @param hotelId The ID of the hotel to be booked.
     * @param arrivalDate The day that the booking should begin (checkin).
     * @param departureDate The day that the booking will end (checkout).
     * @param supplierType The supplierType (found in the list call, usually "E").
     * @param roomGroup The map of names and rooms that will be booked. Name refers to the person who will check in.
     * @param reservationInfo The information about the entity making the reservation. "Billing" information.
     * @param addressInfo The address associated with the reservationInfo.
     * @throws IOException If there is some sort of network error while making the booking.
     * @throws EanWsError If there is an error on the EAN API side with the booking. Often caused by incorrect input.
     */
    public static void performBooking(final Long hotelId,
                                      final DateTime arrivalDate,
                                      final DateTime departureDate,
                                      final String supplierType,
                                      final Map<Name, HotelRoom> roomGroup,
                                      final ReservationInfo reservationInfo,
                                      final Map<String, String> addressInfo)
            throws IOException, EanWsError {
        final HotelRoom firstRoom = roomGroup.entrySet().iterator().next().getValue();
        final List<NameValuePair> rateInfoParameters = Arrays.<NameValuePair>asList(
            new BasicNameValuePair("hotelId", hotelId.toString()),
            new BasicNameValuePair("arrivalDate", formatApiDate(arrivalDate)),
            new BasicNameValuePair("departureDate", formatApiDate(departureDate)),
            new BasicNameValuePair("supplierType", supplierType),
            new BasicNameValuePair("rateKey", firstRoom.rate.roomGroup.get(0).rateKey),
            new BasicNameValuePair("roomTypeCode", firstRoom.roomTypeCode),
            new BasicNameValuePair("rateCode", firstRoom.rateCode),
            new BasicNameValuePair("chargeableRate", "")
        );
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(BASIC_URL_PARAMETERS);
        urlParameters.addAll(rateInfoParameters);
        urlParameters.addAll(roomGroupAsNameValuePairs(roomGroup));
        urlParameters.addAll(reservationInfoAsNameValuePairs(reservationInfo));
        urlParameters.addAll(addressInfoAsNameValuePairs(addressInfo));

        try {
            final JSONObject json = performApiRequest(URL_SUBDIR, urlParameters);
        } catch (JSONException jse) {
            throw new JsonParsingException(jse);
        }
    }

    /**
     * Gets the NameValuePairs that represent a room group for transmission over the wire.
     * @param roomGroup The Map of Names and HotelRooms to "serialize".
     * @return The requested list of NameValuePairs.
     */
    private static List<NameValuePair> roomGroupAsNameValuePairs(final Map<Name, HotelRoom> roomGroup) {
        String roomId;
        final int numberOfBitsPerRoom = 5;
        final List<NameValuePair> roomBits = new ArrayList<NameValuePair>(roomGroup.size() * numberOfBitsPerRoom);
        for (Map.Entry<Name, HotelRoom> entry : roomGroup.entrySet()) {
            roomId = "room" + roomBits.size() + 1;
            roomBits.add(new BasicNameValuePair(roomId, getAdultsAndChildren(entry.getValue())));
            roomBits.add(new BasicNameValuePair(roomId + "FirstName", entry.getKey().first));
            roomBits.add(new BasicNameValuePair(roomId + "LastName", entry.getKey().last));
            roomBits.add(new BasicNameValuePair(roomId + "BedTypeId", entry.getValue().bedTypeId));
            roomBits.add(new BasicNameValuePair(roomId + "SmokingPreference", entry.getValue().smokingPreference));
        }

        return Collections.unmodifiableList(roomBits);
    }

    /**
     * Gets the string representing the number of adults and children for a room in the abbreviated syntax.
     * @param hotelRoom The room whose occupancy is being shown.
     * @return The adults and children, represented as [numberOfAdults],[childAge],[childAge],...
     */
    private static String getAdultsAndChildren(final HotelRoom hotelRoom) {
//        StringBuilder adultsAndChildren = new StringBuilder((hotelRoom.childrensAges.size() * 2) + 1);
//        adultsAndChildren.append(hotelRoom.numberOfAdults);
//        for (int childAge : hotelRoom.childrensAges) {
//            adultsAndChildren.append(",");
//            adultsAndChildren.append(childAge);
//        }
//        return adultsAndChildren.toString();
        return "1";
    }

    /**
     * Gets NameValuePairs for the reservation information so it can be sent in a rest request.
     * @param reservationInfo The information to turn into NameValuePairs
     * @return The requested NameValuePairs
     */
    private static List<NameValuePair> reservationInfoAsNameValuePairs(final ReservationInfo reservationInfo) {
        return Arrays.<NameValuePair>asList(
                new BasicNameValuePair("email", reservationInfo.email),
                new BasicNameValuePair("firstName", reservationInfo.firstName),
                new BasicNameValuePair("lastName", reservationInfo.lastName),
                new BasicNameValuePair("homePhone", reservationInfo.homePhone),
                new BasicNameValuePair("workPhone", reservationInfo.workPhone),
                new BasicNameValuePair("creditCardType", reservationInfo.creditCardType),
                new BasicNameValuePair("creditCardNumber", reservationInfo.creditCardNumber),
                new BasicNameValuePair("creditCardIdentifier", reservationInfo.creditCardIdentifier),
                new BasicNameValuePair("creditCardExpirationMonth", reservationInfo.creditCardExpirationMonth),
                new BasicNameValuePair("creditCardExpirationYear", reservationInfo.creditCardExpirationYear)
        );
    }

    /**
     * Gets NameValuePairs for the address information so it can be sent in a rest request.
     * @param addressInfo The information to turn into NameValuePairs
     * @return The requested NameValuePairs
     */
    private static List<NameValuePair> addressInfoAsNameValuePairs(final Map<String, String> addressInfo) {
        final List<NameValuePair> addressInfoPairs = new ArrayList<NameValuePair>(addressInfo.size());
        for (Map.Entry<String, String> entry : addressInfo.entrySet()) {
            addressInfoPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return addressInfoPairs;
    }
}
