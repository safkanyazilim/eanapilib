/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.hotel;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a reservation's cancellation policy as returned from the api.
 */
public final class CancellationPolicy {
    /**
     * The text of this particular cancellation policy. Provides a human-readable description of the cancellation
     * policy for the reservation.
     *
     * This text is the final word on the cancellation policy. In any conflict with the returned
     * CancellationPolicyInformation objects associated with this policy, the text always supercedes
     * the discrete CancellationPolicyInformation objects.
     */
    public final String text;

    /**
     * The more machine-friendly cancellation policy information. Describes exactly when a reservation can be cancelled.
     */
    public final List<CancellationPolicyInformation> policies;

    /**
     * The standard constructor for cancellation policies, created from the json and the arrival date of the
     * reservation.
     * @param object The JSONObject from which to construct.
     * @param arrivalDate The arrivalDate of the reservation.
     */
    public CancellationPolicy(final JSONObject object, final LocalDate arrivalDate) {
        this.text = object.optString("cancellationPolicy");

        final List<CancellationPolicyInformation> localPolicies;
        if (object.has("CancelPolicyInfoList")) {
            final JSONObject listJson = object.optJSONObject("CancelPolicyInfoList");
            if (listJson.optJSONArray("CancelPolicyInfo") != null) {
                localPolicies = new LinkedList<CancellationPolicyInformation>();
                final JSONArray infoListJson = listJson.optJSONArray("CancelPolicyInfo");
                for (int i = 0; i < infoListJson.length(); i++) {
                    localPolicies.add(new CancellationPolicyInformation(infoListJson.optJSONObject(i), arrivalDate));
                }
            } else {
                localPolicies = Collections.singletonList(
                    new CancellationPolicyInformation(listJson.optJSONObject("CancelPolicyInfo"), arrivalDate));
            }
        } else {
            localPolicies = Collections.emptyList();
        }

        this.policies = Collections.unmodifiableList(localPolicies);
    }

    /**
     * Simply the text of the cancellation policy.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Cancellation policy information as described in the
     * <a href="http://developer.ean.com/general_info/CancelPolicyInfo_Array">Cancel Policy Info Array</a>
     * documentation.
     */
    public final class CancellationPolicyInformation {

        /**
         * The last instant which this cancellation policy is applicable. Calculated using the cancelTime,
         * startWindowHours, timeZoneDescription, and the arrival date for the reservation.
         */
        public final DateTime lastInstantApplicable;

        /**
         * The number of nights charged as a penalty for cancelling within the policy window.
         * The value of a night is the same as the highest nightly rate when the booking was placed.
         */
        public final BigDecimal nightCount;

        /**
         * Percentage of the value of the total cost of stay(less taxes and fees)
         * that will be charged as a penalty for cancelling within the policy window.
         */
        public final BigDecimal percent;

        /**
         * Flat fee to be charged as a penalty for cancelling within the policy window.
         */
        public final BigDecimal amount;

        /**
         * Currency code penalties will be charged in.
         */
        public final String currencyCode;

        /**
         * The standard constructor for the cancel policy info.
         * @param object The object to construct from.
         * @param arrivalDate The arrival date of the reservation.
         */
        public CancellationPolicyInformation(final JSONObject object, final LocalDate arrivalDate) {
            this.currencyCode = object.optString("currencyCode");

            final Double localNightCount = object.optDouble("nightCount");
            final Double localPercent = object.optDouble("percent");
            final Double localAmount = object.optDouble("amount");

            this.nightCount = localNightCount == null ? null : BigDecimal.valueOf(localNightCount);
            this.percent = localPercent == null ? null : BigDecimal.valueOf(localPercent);
            this.amount = localAmount == null ? null : BigDecimal.valueOf(localAmount);

            final String cancelTimeString = object.optString("cancelTime");
            final int startWindowHours = object.optInt("startWindowHours");
            final String timeZoneDescription = object.optString("timeZoneDescription");
            final String timeZoneId = timeZoneDescription.substring(4, 6);

            final LocalTime cancelTime = LocalTime.parse(cancelTimeString);
            final DateTimeZone cancelZone;

            if (timeZoneId.startsWith("+") || timeZoneId.startsWith("-")) {
                cancelZone = DateTimeZone.forID(timeZoneId);
            } else {
                cancelZone = DateTimeZone.forID("UTC");
            }

            this.lastInstantApplicable
                = arrivalDate.toDateTime(cancelTime).withZone(cancelZone).minusHours(startWindowHours);

        }
    }
}
