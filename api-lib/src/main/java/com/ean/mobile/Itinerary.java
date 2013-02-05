/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Holder for itinerary information returned from the booking or itinerary requests.
 * Built from documentation found in the documentation for
 * <a href="http://developer.ean.com/docs/read/hotels/version_3/book_reservation/Response_Format">
 *     reservation responses </a> and
 * <a href="http://developer.ean.com/docs/read/hotels/version_3/request_itinerary/Response_Format">
 *     itinerary responses</a>.
 *
 * Most of the javadoc comes from the documentation found on these pages as well.
 */
public final class Itinerary {

    /**
     * The formatter used for paring DateTime objects from returned api date fields.
     */
    private static final DateTimeFormatter API_DATE_PARSER = DateTimeFormat.forPattern("MM/dd/YYYY");

    /**
     * ID associated with the booking.
     */
    public final long id;

    /**
     * CID credited for the booking.
     */
    public final long affiliateId;

    /**
     * Date reservation was booked.
     */
    public final LocalDate creationDate;

    /**
     * First check-in date for this itinerary.
     */
    public final LocalDate itineraryStartDate;

    /**
     * Final check-out date for this itinerary.
     */
    public final LocalDate itineraryEndDate;

    /**
     * Affiliate-assigned value used when booking was placed, if applicable.
     */
    public final String affiliateCustomerId;

    /**
     * Information about the customer associated with the booking. That is, the customer who was the cardholder
     * of the payment card used to make the booking.
     */
    public final Customer customer;

    /**
     * The list of confirmations associated with this itinerary.
     */
    public final List<HotelConfirmation> hotelConfirmations;

    /**
     * Constructs an Itinerary object from an appropriately structured JSONObject.
     * @param object The JSONObject that contains appropriate fields to construct an Itinerary.
     */
    public Itinerary(final JSONObject object) {
        this.id = object.optLong("itineraryId");
        this.affiliateId = object.optLong("affiliateId");
        this.creationDate = API_DATE_PARSER.parseLocalDate(object.optString("creationDate"));
        this.itineraryStartDate = API_DATE_PARSER.parseLocalDate(object.optString("itineraryStartDate"));
        this.itineraryEndDate = API_DATE_PARSER.parseLocalDate(object.optString("itineraryEndDate"));
        this.affiliateCustomerId = object.optString("affiliateCustomerId");
        this.customer = new Customer(object.optJSONObject("Customer"));

        final List<HotelConfirmation> confirmations;
        if (object.optJSONArray("HotelConfirmation") != null) {
            final JSONArray confirmationsJson = object.optJSONArray("HotelConfirmation");
            confirmations = new ArrayList<HotelConfirmation>(confirmationsJson.length());
            for (int i = 0; i < confirmationsJson.length(); i++) {
                confirmations.add(new HotelConfirmation(confirmationsJson.optJSONObject(i)));
            }
        } else {
            confirmations = Collections.singletonList(new HotelConfirmation(object.optJSONObject("HotelConfirmation")));
        }
        this.hotelConfirmations = Collections.unmodifiableList(confirmations);
    }

    /**
     * Contains all information for the customer associated with the booking.
     * In this case customer = cardholder of the payment card used to create the booking.
     */
    public final class Customer extends Individual {

        /**
         * The customer's telephone extension, if provided.
         */
        public final String extension;

        /**
         * The customer's fax number, if provided.
         */
        public final String faxPhone;

        /**
         * The customer's address information.
         */
        public final CustomerAddress address;

        /**
         * Constructs a customer object from a JSONObject which has all of the fields for an
         * {@link Individual} plus extension, faxPhone, and a {@link CustomerAddress}.
         * @param object The JSONObject with the appropriate fields.
         */
        public Customer(final JSONObject object) {
            super(object);
            this.extension = object.has("extension") ? object.optString("extension") : null;
            this.faxPhone = object.has("faxPhone") ? object.optString("faxPhone") : null;
            final JSONObject customerAddressObject
                = object.has("CustomerAddress")
                ? object.optJSONObject("CustomerAddress")
                : object.optJSONObject("CustomerAddresses");
            this.address = new CustomerAddress(customerAddressObject);
        }

    }

    /**
     * Contains information on the room and hotel booked as well as the current status of the itinerary.
     * @see
     * <a href="http://developer.ean.com/docs/read/hotels/version_3/request_itinerary/Response_Format">
     * EAN Hotel Confirmation documentation.</a>
     */
    public final class HotelConfirmation {

        /**
         * The id of the supplier used to book the hotel.
         */
        public final int supplierId;

        /**
         * The chain code of the hotel. May be null.
         */
        public final String chainCode;

        /**
         * The type of credit card used to make the booking.
         */
        public final String creditCardType;

        /**
         * The check-in date for this particular confirmation.
         */
        public final LocalDate arrivalDate;

        /**
         * The check-out date for this particular confirmation.
         */
        public final LocalDate departureDate;

        /**
         * The confirmation number for this particular confirmation.
         */
        public final String confirmationNumber;

        /**
         * The cancellation number for this particular confirmation. May be null.
         */
        public final String cancellationNumber;

        /**
         * The stated occupancy of this confirmation.
         */
        public final RoomOccupancy occupancy;

        /**
         * The cancellation policy text for this confirmation. Notes the restrictions regarding cancellation.
         * availability for the booking.
         */
        public final String cancellationPolicy;

        /**
         * The affiliate-determined confirmationID, if sent during the booking process.
         */
        public final String affiliateConfirmationId;

        /**
         * The smoking preference, if indicated on original booking.
         */
        public final String smokingPreference;

        /**
         * The room type code for the room booked.
         * {@link HotelRoom#roomTypeCode}
         */
        public final String roomTypeCode;

        /**
         * The rate code for the room booked.
         * {@link HotelRoom#rateCode}
         */
        public final String rateCode;

        /**
         * The rate description for the confirmation.
         */
        public final String rateDescription;

        /**
         * The room description for the room of this confirmation.
         */
        public final String roomDescription;

        /**
         * The status of this confirmation.
         */
        public final ConfirmationStatus status;

        /**
         * Indicates if the booking is non-refundable.
         */
        public final boolean nonRefunable;

        /**
         * The locale used to place the booking.
         */
        public final String locale;

        /**
         * The number of nights booked.
         */
        public final int nights;

        /**
         * The rate information from the original booking request.
         */
        public final Rate rate;

        /**
         * The name of the guest who is staying in this particular hotel room.
         */
        public final Name guestName;

        /**
         * Constructs a HotelConfirmation object from an appropriately structured JSONObject.
         * @param object A JSONObject with the appropriate fields.
         */
        public HotelConfirmation(final JSONObject object) {
            this.supplierId = object.optInt("supplierId");
            this.chainCode = object.optString("chainCode");
            this.creditCardType = object.optString("creditCardType");
            this.arrivalDate = API_DATE_PARSER.parseLocalDate(object.optString("arrivalDate"));
            this.departureDate = API_DATE_PARSER.parseLocalDate(object.optString("departureDate"));
            this.confirmationNumber = object.optString("confirmationNumber");
            this.cancellationNumber = object.optString("cancellationNumber");
            this.occupancy = new RoomOccupancy(object);
            this.cancellationPolicy = object.optString("cancellationPolicy");
            this.affiliateConfirmationId = object.optString("affiliateConfirmationId");
            this.smokingPreference = object.optString("smokingPreference");
            this.roomTypeCode = object.optString("roomTypeCode");
            this.rateCode = object.optString("rateCode");
            this.rateDescription = object.optString("rateDescription");
            this.roomDescription = object.optString("roomDescription");
            this.status = ConfirmationStatus.fromString(object.optString("status"));
            this.nonRefunable = object.optBoolean("nonRefundable");
            this.locale = object.optString("locale");
            this.nights = object.optInt("nights");
            this.rate = Rate.parseFromRateInformations(object).get(0);
            this.guestName = new Name(object.optJSONObject("ReservationGuest"));

        }

        /**
         * Represents the data held in an itinerary for a hotel.
         */
        public final class Hotel {
            /**
             * The id of the hotel that has been booked.
             */
            public final long id;

            /**
             * The status code of the hotel.
             * The current status of the hotel in EAN's database at the time of the itinerary request.
             * Any status other than A cannot be rebooked.
             * Values:
             *      A:Active
             *      I:Inactive
             *      D:Deleted
             *      R:Removed
             *      C:Confidenced
             */
            public final String statusCode;

            /**
             * The address of the hotel, including its lat/long.
             */
            public final LatLongAddress address;

            //Phone and fax are left out since these should not be interacted with normally.

            /**
             * The statistical low rate of the hotel (not necessarily of the booking).
             */
            public final BigDecimal lowRate;

            /**
             * The statistical high rate of the hotel (not necessarily of the booking).
             */
            public final BigDecimal highRate;

            /**
             * Confidence rating of the hotel, for Hotel Collect properties. May be null.
             */
            public final Double confidence;

            /**
             * The hotel's star rating.
             * {@link com.ean.mobile.Hotel#starRating}.
             */
            public final BigDecimal starRating;

            /**
             * The market the booking was applied to, e.g. Los Angeles. May be null.
             */
            public final String market;

            /**
             * Regional market the booking was applied to, e.g. California. May be null.
             */
            public final String region;

            /**
             * Returns the superregion the booking was applied to, e.g. AMER. May be null.
             */
            public final String superRegion;

            /**
             * Theme of the property booked, e.g. beach hotel, spa hotel, etc. May be null.
             */
            public final String theme;

            /**
             * Any confirmation extra fields requested during the booking. May be null.
             */
            public final Map<String, String> confirmationExtras;

            /**
             * Constructs a HotelObject from a Hotel json object returned from an Itinerary call.
             * @param object The JSONObject appropriately structured.
             */
            public Hotel(final JSONObject object) {
                this.id = object.optLong("hotelId");
                this.statusCode = object.optString("statusCode");
                this.address = new LatLongAddress(object);
                this.lowRate = new BigDecimal(object.optString("lowRate"));
                this.highRate = new BigDecimal(object.optString("highRate"));
                this.confidence = object.optDouble("confidence");
                this.starRating = com.ean.mobile.Hotel.parseStarRating(object.optString("hotelRating"));
                this.market = object.optString("market");
                this.region = object.optString("region");
                this.superRegion = object.optString("superRegion");
                this.theme = object.optString("theme");

                Map<String, String> localConfirmationExtras;
                if (object.has("ConfirmationExtras")) {
                    final JSONArray extras;
                    if ((extras
                            = object.optJSONObject("ConfirmationExtras").optJSONArray("ConfirmationExtra")) != null) {
                        localConfirmationExtras = new HashMap<String, String>();
                        JSONObject thisExtra;
                        for (int i = 0; i < extras.length(); i++) {
                            thisExtra = extras.optJSONObject(i);
                            localConfirmationExtras.put(thisExtra.optString("name"), thisExtra.optString("value"));
                        }
                    } else {
                        final JSONObject thisExtra
                                = object.optJSONObject("ConfirmationExtras").optJSONObject("ConfirmationExtra");
                        localConfirmationExtras
                                = Collections.singletonMap(thisExtra.optString("name"), thisExtra.optString("value"));
                    }
                } else {
                    localConfirmationExtras = Collections.emptyMap();
                }
                this.confirmationExtras = Collections.unmodifiableMap(localConfirmationExtras);
            }
        }
    }
}
