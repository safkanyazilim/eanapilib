/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import java.math.BigDecimal;

import android.widget.ImageView;
import android.widget.LinearLayout;

public final class StarRating {

    public static void populate(final LinearLayout parent, final BigDecimal starRating) {
        final ImageView[] stars = {
            (ImageView) parent.findViewById(R.id.hotelStar1),
            (ImageView) parent.findViewById(R.id.hotelStar2),
            (ImageView) parent.findViewById(R.id.hotelStar3),
            (ImageView) parent.findViewById(R.id.hotelStar4),
            (ImageView) parent.findViewById(R.id.hotelStar5),
        };
        //Log.d("EANDebug starrating", starRating);
        //Log.d("EANDebug starbitslen", starBits.length+"");
        int fullStars = starRating == null ? 0 : starRating.intValue();
        //Log.d("EANDebug starbitsfullstar", fullStars+"");
        boolean halfStars = starRating != null && starRating.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;
        //Log.d("EANDebug starbitshalfstar", halfStars+"");

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
