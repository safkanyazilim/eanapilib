package com.ean.mobile;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a reservation's cancellation policy as returned from the api.
 */
public final class CancellationPolicy {
    /**
     * The text of this particular cancellation policy. Provides a human-readable description of the cancellation
     * policy for the reservation.
     *
     * This text is the final word on the cancellation policy. In any conflict with the returned CancellationPolicyInfo
     * objects associated with this policy, the text always supercedes the discrete CancellationPolicyInfo objects.
     */
    public final String text;

    /**
     * The more machine-friendly cancellation policy information. Describes exactly when a reservation can be cancelled.
     */
    public final List<CancellationPolicyInfo> policies;

    public CancellationPolicy(final JSONObject object, final LocalDate arrivalDate) {
        this.text = object.optString("cancellationPolicy");

        final List<CancellationPolicyInfo> policies;
        if (object.has("CancelPolicyInfoList")) {
            final JSONObject listJson = object.optJSONObject("CancelPolicyInfoList");
            if (listJson.optJSONArray("CancelPolicyInfo") != null) {
                policies = new LinkedList<CancellationPolicyInfo>();
                final JSONArray infoListJson = listJson.optJSONArray("CancelPolicyInfo");
                for (int i = 0; i < infoListJson.length(); i++) {
                    policies.add(new CancellationPolicyInfo(infoListJson.optJSONObject(i), arrivalDate));
                }
            } else {
                policies = Collections.singletonList(
                        new CancellationPolicyInfo(listJson.optJSONObject("CancelPolicyInfo"), arrivalDate));
            }
        } else {
            policies = Collections.emptyList();
        }

        this.policies = Collections.unmodifiableList(policies);
    }

    /**
     * Cancellation policy information as described in the
     * <a href="http://developer.ean.com/general_info/CancelPolicyInfo_Array">Cancel Policy Info Array</a>
     * documentation.
     */
    public final class CancellationPolicyInfo {
        /**
         * Version ID value returned by the api.
         */
        public final int versionId;

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

        public CancellationPolicyInfo(final JSONObject object, final LocalDate arrivalDate) {
            this.versionId = object.optInt("versionId");
            this.currencyCode = object.optString("currencyCode");

            final Double nightCount = object.optDouble("nightCount");
            final Double percent = object.optDouble("percent");
            final Double amount = object.optDouble("amount");

            this.nightCount = nightCount == null ? null : BigDecimal.valueOf(nightCount);
            this.percent = percent == null ? null : BigDecimal.valueOf(percent);
            this.amount = amount == null ? null : BigDecimal.valueOf(amount);

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

    /**
     * Simply the text of the cancellation policy.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text;
    }
}
