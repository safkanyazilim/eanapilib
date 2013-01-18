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
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import com.ean.mobile.Address;
import com.ean.mobile.Individual;
import com.ean.mobile.Reservation;
import com.ean.mobile.ReservationRoom;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;

/**
 * The class that performs booking requests.
 */
public final class BookingRequest extends Request<Reservation> {

    /**
     * This method actually performs the booking request with the passed room and occupancy information.
     * @param hotelId The ID of the hotel to be booked.
     * @param arrivalDate The day that the booking should begin (checkin).
     * @param departureDate The day that the booking will end (checkout).
     * @param supplierType The supplierType (found in the list call, usually "E").
     * @param room The room and its occupancy that will be booked.
     * @param reservationInfo The information about the entity making the reservation. "Billing" information.
     * @param address The address associated with the reservationInfo.
     * @param customerSessionId The sessionID associated with this search session.
     * @param locale The locale in which to book the hotel.
     * @param currencyCode The currency code in which to book the hotel. Must be chargeable, enforced by EAN API.
     * @throws IOException If there is some sort of network error while making the booking.
     * @throws EanWsError If there is an error on the EAN API side with the booking. Often caused by incorrect input.
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     */
    public BookingRequest(final Long hotelId,
                          final LocalDate arrivalDate, final LocalDate departureDate, final String supplierType,
                          final ReservationRoom room, final ReservationInfo reservationInfo, final Address address,
                          final String customerSessionId, final String locale, final String currencyCode)
            throws IOException, EanWsError, UrlRedirectionException {
        this(hotelId, arrivalDate, departureDate, supplierType, Collections.singletonList(room),
                reservationInfo, address, customerSessionId, locale, currencyCode);
    }

    /**
     * This method actually performs the booking request with the passed room and occupancy information.
     * @param hotelId The ID of the hotel to be booked.
     * @param arrivalDate The day that the booking should begin (checkin).
     * @param departureDate The day that the booking will end (checkout).
     * @param supplierType The supplierType (found in the list call, usually "E").
     * @param roomGroup The Rooms and their occupancies that will be booked.
     * @param reservationInfo The information about the entity making the reservation. "Billing" information.
     * @param address The address associated with the reservationInfo.
     * @param customerSessionId Customer session id, passed to track user as they move through
     *  the booking flow.
     * @param locale The locale in which to book the hotel.
     * @param currencyCode The currency code in which to book the hotel. Must be chargeable, enforced by EAN API.
     * @throws IOException If there is some sort of network error while making the booking.
     * @throws EanWsError If there is an error on the EAN API side with the booking. Often caused by incorrect input.
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     */
    public BookingRequest(final Long hotelId, final LocalDate arrivalDate, final LocalDate departureDate,
            final String supplierType, final List<ReservationRoom> roomGroup, final ReservationInfo reservationInfo,
            final Address address, final String customerSessionId, final String locale, final String currencyCode)
            throws IOException, EanWsError, UrlRedirectionException {

        this(hotelId, arrivalDate, departureDate, supplierType, roomGroup, reservationInfo, address,
                customerSessionId, null, locale, currencyCode);
    }

    /**
     * This method actually performs the booking request with the passed room and occupancy information.
     * @param hotelId The ID of the hotel to be booked.
     * @param arrivalDate The day that the booking should begin (checkin).
     * @param departureDate The day that the booking will end (checkout).
     * @param supplierType The supplierType (found in the list call, usually "E").
     * @param roomGroup The Rooms and their occupancies that will be booked.
     * @param reservationInfo The information about the entity making the reservation. "Billing" information.
     * @param address The address associated with the reservationInfo.
     * @param customerSessionId The session ID carried over from the original search.
     * @param extraBookingData Any extra parameters (like confirmation extra, etc.) to pass to the booking request.
     * @param locale The locale in which to book the hotel.
     * @param currencyCode The currency code in which to book the hotel. Must be chargeable, enforced by EAN API.
     * @throws IOException If there is some sort of network error while making the booking.
     * @throws EanWsError If there is an error on the EAN API side with the booking. Often caused by incorrect input.
     * @throws UrlRedirectionException If the network connection was unexpectedly redirected.
     */
    public BookingRequest(final Long hotelId, final LocalDate arrivalDate,
            final LocalDate departureDate, final String supplierType, final List<ReservationRoom> roomGroup,
            final ReservationInfo reservationInfo, final Address address, final String customerSessionId,
            final List<NameValuePair> extraBookingData, final String locale, final String currencyCode)
            throws IOException, EanWsError, UrlRedirectionException {

        final List<NameValuePair> rateInfoParameters = Arrays.<NameValuePair>asList(
                new BasicNameValuePair("customerSessionId", customerSessionId),
                new BasicNameValuePair("hotelId", hotelId.toString()),
                new BasicNameValuePair("supplierType", supplierType)
        );

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.addAll(getBasicUrlParameters(locale, currencyCode, arrivalDate, departureDate));
        urlParameters.addAll(rateInfoParameters);
        urlParameters.addAll(ReservationRoom.asNameValuePairs(roomGroup));
        urlParameters.addAll(reservationInfo.asNameValuePairs());
        urlParameters.addAll(address.asBookingRequestPairs());
        urlParameters.addAll(extraBookingData == null ? Collections.<NameValuePair>emptyList() : extraBookingData);

        setUrlParameters(urlParameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reservation consume(final JSONObject jsonObject) throws JSONException, EanWsError {
        if (jsonObject == null) {
            return null;
        }

        final JSONObject response = jsonObject.getJSONObject("HotelListResponse");

        if (response.has("EanWsError")) {
            System.out.println(response.toString());
            //TODO: THIS HAS TO BE HANDLED DIFFERENTLY.
            throw EanWsError.fromJson(response.getJSONObject("EanWsError"));
        }
        //TODO: make itinerary objects, cache them, and some logic handling the reservationStatusCode.
        return new Reservation(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return "res";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSecure() {
        return true;
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
        public ReservationInfo(final String email, final String firstName, final String lastName,
                final String homePhone, final String workPhone, final String creditCardType,
                final String creditCardNumber, final String creditCardIdentifier,
                final YearMonth creditCardExpirationDate) {
            this.individual = new BookingIndividual(email, firstName, lastName, homePhone, workPhone);
            this.creditCardInfo = new CreditCardInfo(
                creditCardType, creditCardNumber, creditCardIdentifier, creditCardExpirationDate);
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
    public static final class BookingIndividual extends Individual {
        /**
         * The constructor for the holder for information about a particular individual.
         * @param email The individual's email.
         * @param firstName The individual's first name.
         * @param lastName The individual's last name.
         * @param homePhone The individual's home telephone number.
         * @param workPhone The individual's work telephone number.
         */
        public BookingIndividual(final String email, final String firstName, final String lastName,
                final String homePhone, final String workPhone) {
            super(email, firstName, lastName, homePhone, workPhone);
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
        private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormat.forPattern("yyyy");

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
        public final YearMonth expirationDate;

        /**
         * The sole constructor for the holder for credit card information.
         * @param type The credit card's type, see {@link CreditCardInfo#type}.
         * @param number The credit card's number.
         * @param identifier The credit card's identifier.
         * @param expirationDate The credit card's expiration date.
         */
        public CreditCardInfo(
                final String type, final String number, final String identifier, final YearMonth expirationDate) {
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

}
