/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


/**
 * Data holder for information about a particular individual.
 * DO NOT SERIALIZE OR SAVE ANYWHERE.
 */
public abstract class Individual {


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

    public Individual(final JSONObject object) {
        this.email = object.optString("email");
        this.name = new Name(object);
        this.homePhone = object.optString("homePhone");
        this.workPhone = object.optString("workPhone");
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
                homePhone == null ? null : new BasicNameValuePair("homePhone", homePhone),
                workPhone == null ? null : new BasicNameValuePair("workPhone", workPhone)
        );
    }
}
