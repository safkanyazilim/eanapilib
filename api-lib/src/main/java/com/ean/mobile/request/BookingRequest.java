/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;


import com.ean.mobile.HotelRoom;
import com.ean.mobile.Rate;
import com.ean.mobile.RoomOccupancy;
import com.ean.mobile.exception.EanWsError;

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
     * @param roomGroup The Rooms and their occupancies that will be booked.
     * @param reservationInfo The information about the entity making the reservation. "Billing" information.
     * @param addressInfo The address associated with the reservationInfo.
     * @throws IOException If there is some sort of network error while making the booking.
     * @throws EanWsError If there is an error on the EAN API side with the booking. Often caused by incorrect input.
     */
    public static void performBooking(final Long hotelId,
                                      final DateTime arrivalDate,
                                      final DateTime departureDate,
                                      final String supplierType,
                                      final List<Room> roomGroup,
                                      final ReservationInfo reservationInfo,
                                      final Map<String, String> addressInfo)
            throws IOException, EanWsError {
        final Room firstRoom = roomGroup.get(0);
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
        urlParameters.addAll(Room.asNameValuePairs(roomGroup));
        urlParameters.addAll(reservationInfo.asNameValuePairs());
        urlParameters.addAll(addressInfoAsNameValuePairs(addressInfo));

        try {
            final JSONObject json = performApiRequest(URL_SUBDIR, urlParameters);
        } catch (JSONException jse) {
            return;
        }
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


    /**
     * This is a holder for all of the information required as "billing" information in the booking process.
     */
    public static final class ReservationInfo {

        /**
         * The individual for which this reservation will be made.
         */
        public final Individual individual;

        /**
         * The credit card information to be used by the individual.
         */
        public final CreditCardInfo creditCardInfo;

        /**
         * The main constructor for a reservation information object.
         * @param email The email of the individual.
         * @param firstName The first name of the individual.
         * @param lastName The last name of the individual.
         * @param homePhone The home telephone number of the individual.
         * @param workPhone The work telephone number of the individual.
         * @param creditCardType The credit card type for the booking.
         * @param creditCardNumber The credit card number for the booking.
         * @param creditCardIdentifier The credit card identifier (CCID, CID, etc.)
         * @param creditCardExpirationDate The expiration date of the credit card.
         */
        public ReservationInfo(final String email,
                               final String firstName,
                               final String lastName,
                               final String homePhone,
                               final String workPhone,
                               final String creditCardType,
                               final String creditCardNumber,
                               final String creditCardIdentifier,
                               final DateTime creditCardExpirationDate) {
            this.individual = new Individual(
                    email,
                    firstName,
                    lastName,
                    homePhone,
                    workPhone);
            this.creditCardInfo = new CreditCardInfo(
                    creditCardType,
                    creditCardNumber,
                    creditCardIdentifier,
                    creditCardExpirationDate);
        }

        /**
         * Gets NameValuePairs for the reservation information so it can be sent in a rest request.
         * @return The requested NameValuePairs
         */
        private List<NameValuePair> asNameValuePairs() {

            final List<NameValuePair> out = new ArrayList<NameValuePair>();
            out.addAll(individual.asNameValuePairs());
            out.addAll(creditCardInfo.asNameValuePairs());

            return Collections.unmodifiableList(out);
        }
    }

    /**
     * Data holder for information about a particular individual.
     * DO NOT SERIALIZE OR SAVE ANYWHERE.
     */
    public static final class Individual {


        /**
         * The email of the individual.
         */
        public final String email;

        /**
         * The name of the individual.
         */
        public final Name name;

        /**
         * The individual's home telephone number.
         */
        public final String homePhone;

        /**
         * The individual's work telephone number.
         */
        public final String workPhone;

        /**
         * The constructor for the holder for information about a particular individual.
         * @param email The individual's email.
         * @param firstName The individual's first name.
         * @param lastName The individual's last name.
         * @param homePhone The individual's home telephone number.
         * @param workPhone The individual's work telephone number.
         */
        public Individual(final String email,
                          final String firstName,
                          final String lastName,
                          final String homePhone,
                          final String workPhone) {
            this.email = email;
            this.name = new Name(firstName, lastName);
            this.homePhone = homePhone;
            this.workPhone = workPhone;
        }

        /**
         * Gets NameValuePairs for the reservation information so it can be sent in a rest request.
         * @return The requested NameValuePairs
         */
        public List<NameValuePair> asNameValuePairs() {
            return Arrays.<NameValuePair>asList(
                    new BasicNameValuePair("email", email),
                    new BasicNameValuePair("firstName", name.first),
                    new BasicNameValuePair("lastName", name.last),
                    new BasicNameValuePair("homePhone", homePhone),
                    new BasicNameValuePair("workPhone", workPhone)
            );
        }
    }

    /**
     * A holder for particular information about a credit card used for booking.
     * DO NOT SERIALIZE OR SAVE ANYWHERE.
     */
    public static final class CreditCardInfo {

        /**
         * The formatter for the month field passed to the request.
         */
        private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormat.forPattern("MM");

        /**
         * The formatter for the year field passed to the request.
         */
        private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormat.forPattern("YY");

        /**
         * The type of credit card to be used (MC, VS, etc.).
         */
        public final String type;

        /**
         * The actual credit card number.
         */
        public final String number;

        /**
         * The cid of the credit card.
         */
        public final String identifier;

        /**
         * The card's expiration date, to be formatted later on.
         */
        public final DateTime expirationDate;

        /**
         * The sole constructor for the holder for credit card information.
         * @param type The credit card's type, see {@link CreditCardInfo#type}.
         * @param number The credit card's number.
         * @param identifier The credit card's identifier.
         * @param expirationDate The credit card's expiration date.
         */
        public CreditCardInfo(final String type,
                              final String number,
                              final String identifier,
                              final DateTime expirationDate) {
            this.type = type;
            this.number = number;
            this.identifier = identifier;
            this.expirationDate = expirationDate;
        }

        /**
         * Gets the list of name value pairs representing this credit card for a booking request.
         * @return The list as mentioned above.
         */
        public List<NameValuePair> asNameValuePairs() {
            return Arrays.<NameValuePair>asList(
                    new BasicNameValuePair("creditCardType", type),
                    new BasicNameValuePair("creditCardNumber", number),
                    new BasicNameValuePair("creditCardIdentifier", identifier),
                    new BasicNameValuePair("creditCardExpirationMonth", MONTH_FORMATTER.print(expirationDate)),
                    new BasicNameValuePair("creditCardExpirationYear", YEAR_FORMATTER.print(expirationDate)));
        }
    }

    /**
     * A class that holds the first and last name of an individual in an easy to use container.
     */
    public static final class Name {

        /**
         * The first name of the person represented.
         */
        public final String first;

        /**
         * The last name of the person represented.
         */
        public final String last;

        /**
         * The constructor for this name object.
         * @param first The first name to set.
         * @param last The last name.
         */
        public Name(final String first, final String last) {
            this.first = first;
            this.last = last;
        }
    }

    /**
     * A container for a room for booking.
     */
    public static final class Room {

        /**
         * The name of the individual who will check in this room.
         */
        public final Name checkInName;

        /**
         * The bedtypeId for this room occupancy. Found in the Room object of a list request.
         */
        public final String bedTypeId;

        /**
         * The smoking preference for this room. Found in the Room object of a list request.
         */
        public final String smokingPreference;

        /**
         * {@link HotelRoom#roomTypeCode}.
         */
        public final String roomTypeCode;

        /**
         * {@link HotelRoom#rateCode}.
         */
        public final String rateCode;

        /**
         * {@link HotelRoom#rate}.
         */
        public final Rate rate;

        /**
         * The stated occupancy for this room.
         */
        public final RoomOccupancy occupancy;

        /**
         * The primary constructor setting the final variables in this class.
         * @param checkInName see {@link Room#checkInName}.
         * @param bedTypeId see {@link Room#bedTypeId}.
         * @param smokingPreference see {@link Room#smokingPreference}.
         * @param roomTypeCode see {@link HotelRoom#roomTypeCode}.
         * @param rateCode see {@link HotelRoom#rateCode}.
         * @param rate see {@link HotelRoom#rate}.
         * @param numberOfAdults The number of adults in this occupancy.
         * @param childAges The list of children's ages for this room.
         */
        public Room(final Name checkInName,
                             final String bedTypeId,
                             final String smokingPreference,
                             final String roomTypeCode,
                             final String rateCode,
                             final Rate rate,
                             final int numberOfAdults,
                             final List<Integer> childAges) {
            this.checkInName = checkInName;
            this.bedTypeId = bedTypeId;
            this.smokingPreference = smokingPreference;
            this.rateCode = rateCode;
            this.roomTypeCode = roomTypeCode;
            this.rate = rate;
            this.occupancy = new RoomOccupancy(numberOfAdults, childAges);
        }

        /**
         * The abbreviated constructor taking a room as part of the constructor rather than the individual bits.
         * @param checkInName see {@link Room#checkInName}.
         * @param room The HotelRoom object holding the bedTypeId, smokingPreference, roomTypeCode,
         *             and rateCode for this occupancy.
         * @param numberOfAdults see {@link RoomOccupancy#numberOfAdults}.
         * @param childAges see {@link RoomOccupancy#childAges}.
         */
        public Room(final Name checkInName,
                             final HotelRoom room,
                             final int numberOfAdults,
                             final List<Integer> childAges) {
            this(checkInName,
                    room.bedTypeId,
                    room.smokingPreference,
                    room.rateCode,
                    room.roomTypeCode,
                    room.rate,
                    numberOfAdults,
                    childAges);
        }

        /**
         * Gets the list of NameValuePairs for a list of room occupancies so that they can be easily added to a request.
         * @param rooms The rooms to make into NameValuePairs.
         * @return The NameValuePairs requested, in the natural order of the list, applying room[position] as the
         * name for each NameValuePair
         */
        public static List<NameValuePair> asNameValuePairs(final List<Room> rooms) {
            final List<NameValuePair> pairs = new ArrayList<NameValuePair>(rooms.size());
            String roomId;
            for (Room occupancy : rooms) {
                roomId = "room" + pairs.size() + 1;
                pairs.add(new BasicNameValuePair(roomId, occupancy.occupancy.asAbbreviatedRequestString()));
                pairs.add(new BasicNameValuePair(roomId + "FirstName", occupancy.checkInName.first));
                pairs.add(new BasicNameValuePair(roomId + "LastName", occupancy.checkInName.last));
                pairs.add(new BasicNameValuePair(roomId + "BedTypeId", occupancy.bedTypeId));
                pairs.add(new BasicNameValuePair(roomId + "SmokingPreference", occupancy.smokingPreference));
            }
            return Collections.unmodifiableList(pairs);
        }

    }
}
