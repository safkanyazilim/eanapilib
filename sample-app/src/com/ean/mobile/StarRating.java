/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;

import android.widget.ImageView;
import android.widget.LinearLayout;

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
