/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestTest {

    @Test(expected = MalformedURLException.class)
    public void testCreateFullUrlNullBaseNullParams() throws Exception {
        Request.createFullUrl(null, null);
    }

    @Test
    public void testCreateFullUrlNullParams() throws Exception {
        URL createdURL = Request.createFullUrl("http://hello", null);
        assertEquals("http://hello", createdURL.toString());
    }

    @Test
    public void testCreateFullUrlEmptyParams() throws Exception {
        URL createdURL = Request.createFullUrl("http://hello", Collections.<NameValuePair>emptyList());
        assertEquals("http://hello", createdURL.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFullUrlSingletonNullParams() throws Exception {
        URL createdURL = Request.createFullUrl(
            "http://hello",
            Collections.<NameValuePair>singletonList(new BasicNameValuePair(null, null)));
        assertEquals("http://hello", createdURL.toString());
    }

    @Test
    public void testCreateFullUrlSingletonParamsNullValue() throws Exception {
        URL createdURL = Request.createFullUrl(
            "http://hello",
            Collections.<NameValuePair>singletonList(new BasicNameValuePair("hi", null)));
        assertEquals("http://hello?hi=", createdURL.toString());
    }

    @Test
    public void testCreateFullUrlSingletonParams() throws Exception {
        URL createdURL = Request.createFullUrl(
            "http://hello",
            Collections.<NameValuePair>singletonList(new BasicNameValuePair("hi", "bye")));
        assertEquals("http://hello?hi=bye", createdURL.toString());
    }

    @Test
    public void testCreateFullUrlSingletonParamsEscapableCharacters() throws Exception {
        URL createdURL = Request.createFullUrl(
            "http://hello",
            Collections.<NameValuePair>singletonList(new BasicNameValuePair("hi", "what's that? A BIRD!")));
        assertEquals("http://hello?hi=what%27s+that%3F+A+BIRD%21", createdURL.toString());
    }
}
