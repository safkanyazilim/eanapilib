/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.request.data;

import com.ean.mobile.request.Request;

public final class ReservationInfo {
    public final String email;

    public final String firstName;

    public final String lastName;

    public final String homePhone;

    public final String workPhone;

    public final String creditCardType;

    public final String creditCardNumber;

    public final String creditCardIdentifier;

    public final String creditCardExpirationMonth;

    public final String creditCardExpirationYear;

    public ReservationInfo(final String email,
                           final String firstName,
                           final String lastName,
                           final String homePhone,
                           final String workPhone,
                           final String creditCardType,
                           final String creditCardNumber,
                           final String creditCardIdentifier,
                           final String creditCardExpirationMonth,
                           final String creditCardExpirationYear) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePhone = homePhone;
        this.workPhone = workPhone;
        this.creditCardType = creditCardType;
        this.creditCardNumber = creditCardNumber;
        this.creditCardIdentifier = creditCardIdentifier;
        this.creditCardExpirationMonth = creditCardExpirationMonth;
        this.creditCardExpirationYear = creditCardExpirationYear;
    }

    public String asApiString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Request.xmlIfy("email", email));
        builder.append(Request.xmlIfy("firstName", firstName));
        builder.append(Request.xmlIfy("lastName", lastName));
        builder.append(Request.xmlIfy("homePhone", homePhone));
        builder.append(Request.xmlIfy("workPhone", workPhone));
        builder.append(Request.xmlIfy("creditCardType", creditCardType));
        builder.append(Request.xmlIfy("creditCardNumber", creditCardNumber));
        builder.append(Request.xmlIfy("creditCardIdentifier", creditCardIdentifier));
        builder.append(Request.xmlIfy("creditCardExpirationMonth", creditCardExpirationMonth));
        builder.append(Request.xmlIfy("creditCardExpirationYear", creditCardExpirationYear));

        return Request.xmlIfy("ReservationInfo", builder.toString());
    }
}
