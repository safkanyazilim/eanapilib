/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static com.ean.mobile.ConstantsTest.RegexMatcher.matches;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ConstantsTest {

    @Test
    public void testWithNull() throws Exception {
        assertNull(Constants.SIGNATURE_SECRET);
        assertNull(Whitebox.invokeMethod(Constants.class, "getSignature"));
    }

    @Test
    public void testWithRealValueHashLooksCorrect() throws Exception {
        Constants.SIGNATURE_SECRET = "YOUR-SHARED-SECRET";
        String signature = Whitebox.invokeMethod(Constants.class, "getSignature");

        assertNotNull(signature);
        assertThat(signature, matches("^[a-f0-9]{32}$"));
    }

    @Test
    public void testThatValuesChangeWithTime() throws Exception {
        String signature = Whitebox.invokeMethod(Constants.class, "getSignature");
        Thread.sleep(1100);
        assertThat(signature, not(equalTo(Whitebox.invokeMethod(Constants.class, "getSignature"))));
    }

    /**
     * Hamcrest needs a regex matcher, this provides me with one.
     */
    public static class RegexMatcher extends BaseMatcher<String> {
        private final String regex;

        public RegexMatcher(String regex){
            this.regex = regex;
        }

        public boolean matches(Object o){
            return ((String)o).matches(regex);
        }

        public void describeTo(Description description){
            description.appendText("matches regex=");
        }

        public static RegexMatcher matches(String regex){
            return new RegexMatcher(regex);
        }
    }
}
