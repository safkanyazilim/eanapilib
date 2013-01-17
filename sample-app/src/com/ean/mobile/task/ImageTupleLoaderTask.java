package com.ean.mobile.task;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.ean.mobile.Constants;
import com.ean.mobile.HotelImageTuple;
import com.ean.mobile.SampleConstants;

import java.io.IOException;

public final class ImageTupleLoaderTask extends AsyncTask<HotelImageTuple, Integer, Drawable> {

    private final ImageView view;

    private final boolean loadMain;

    public ImageTupleLoaderTask(final ImageView thumb, final boolean loadMain) {
        super();
        this.view = thumb;
        this.loadMain = loadMain;
    }

    @Override
    protected Drawable doInBackground(HotelImageTuple... hotelImageTuples) {
        try {
            if (loadMain) {
                return hotelImageTuples[0].loadMainImage();
            }
            return hotelImageTuples[0].loadThumbnailImage();
        } catch (IOException ioe) {
            Log.d(SampleConstants.DEBUG, "An error occurred when loading hotel's main thumbnail", ioe);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        view.setImageDrawable(drawable);
    }

}