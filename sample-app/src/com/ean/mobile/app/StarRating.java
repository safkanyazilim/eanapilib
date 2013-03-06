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

package com.ean.mobile.app;

import java.math.BigDecimal;

import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ean.mobile.R;

/**
 * A class to make it easier to populate a star rating layout.
 */
public final class StarRating {

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private StarRating() {
        // see javadoc.
    }

    /**
     * The method that actually populates the star rating layout. Resolves to half stars, rounded down.
     * 1.1 : 1 star
     * 1.9 : 1.5 star
     * 4.5 : 4.5 stars
     * 4.9 : 4.5 stars
     * @param parent The root of the StarRating layout.
     * @param starRating The actual star rating to display.
     */
    public static void populate(final LinearLayout parent, final BigDecimal starRating) {
        final ImageView[] stars = {
            (ImageView) parent.findViewById(R.id.hotelStar1),
            (ImageView) parent.findViewById(R.id.hotelStar2),
            (ImageView) parent.findViewById(R.id.hotelStar3),
            (ImageView) parent.findViewById(R.id.hotelStar4),
            (ImageView) parent.findViewById(R.id.hotelStar5),
        };

        final int fullStars = starRating == null ? 0 : starRating.intValue();
        final boolean halfStars
            = starRating != null && starRating.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;

        for (int i = 0; i < stars.length; i++) {
            if (i < fullStars) {
                stars[i].setImageResource(R.drawable.star);
            } else if (i == fullStars && halfStars) {
                stars[i].setImageResource(R.drawable.halfstar);
            } else {
                stars[i].setImageResource(R.drawable.blankstar);
            }
        }
    }
}
