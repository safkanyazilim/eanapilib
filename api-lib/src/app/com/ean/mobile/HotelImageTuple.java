package com.ean.mobile;

import android.graphics.drawable.Drawable;

public class HotelImageTuple {
    public String thumbnailUrl, mainUrl, caption;
    public Drawable thumbnail, main;

    public HotelImageTuple(String thumbnailUrl, String mainUrl, String caption) {
        this.thumbnailUrl = thumbnailUrl;
        this.mainUrl = mainUrl;
        this.caption = caption;
    }
}
