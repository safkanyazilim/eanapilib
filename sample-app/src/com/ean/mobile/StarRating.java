package com.ean.mobile;

import android.widget.ImageView;
import android.widget.LinearLayout;

import java.math.BigDecimal;

public class StarRating {

    public static void populate(LinearLayout parent,
                                final String starRating) {
        final ImageView[] stars = {
                (ImageView) parent.findViewById(R.id.hotelInfoStar1),
                (ImageView) parent.findViewById(R.id.hotelInfoStar2),
                (ImageView) parent.findViewById(R.id.hotelInfoStar3),
                (ImageView) parent.findViewById(R.id.hotelInfoStar4),
                (ImageView) parent.findViewById(R.id.hotelInfoStar5),
        };
        //Log.d("EANDebug starrating", starRating);
        //Log.d("EANDebug starbitslen", starBits.length+"");
        int fullStars = new BigDecimal(starRating).intValue();
        //Log.d("EANDebug starbitsfullstar", fullStars+"");
        boolean halfStars = starRating.length() > 2;
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
