/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
 */

package com.ean.mobile.hotel;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.junit.Test;

import com.ean.mobile.JSONFileUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NightlyRateTest {

    private static final JSONObject TEST_JSON = JSONFileUtil.loadJsonFromFile("NightlyRateTest.json");

    @Test
    public void testStandardPromo() {
        assertNightlyRate("standard-promo", true, "104.5", "204.5", NightlyRate.PromoType.STANDARD);
    }

    @Test
    public void testMobilePromo() {
        assertNightlyRate("mobile-promo", true, "104.5", "204.5", NightlyRate.PromoType.MOBILE);
    }

    @Test
    public void testNonPromo() {
        assertNightlyRate("non-promo", false, "104.5", "104.5", null);
    }

    @Test
    public void testUnrecognizedPromo() {
        assertNightlyRate("unrecognized-promo", true, "104.5", "204.5", null);
    }

    private void assertNightlyRate(final String jsonName,
                                   final Boolean promo,
                                   final String rate,
                                   final String baseRate,
                                   final NightlyRate.PromoType promoType) {

        JSONObject nightlyRateJson = TEST_JSON.optJSONObject(jsonName);

        assertNotNull(nightlyRateJson);

        NightlyRate nightlyRate = new NightlyRate(nightlyRateJson);
        if (promo) {
            assertTrue(nightlyRate.promo);
        } else {
            assertFalse(nightlyRate.promo);
        }
        assertEquals(new BigDecimal(rate).setScale(2), nightlyRate.rate);
        assertEquals(new BigDecimal(baseRate).setScale(2), nightlyRate.baseRate);
        assertEquals(promoType, nightlyRate.promoType);
    }
}
