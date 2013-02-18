/*
 * Copyright (c) 2013 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ean.mobile.Destination;
import com.ean.mobile.SampleConstants;
import com.ean.mobile.exception.EanWsError;
import com.ean.mobile.exception.UrlRedirectionException;
import com.ean.mobile.request.DestinationRequest;
import com.ean.mobile.request.RequestProcessor;

/**
 * Gets suggestions to display based on the current contents of the search bar.
 */
public final class SuggestionFactory {
    private static SuggestDestinationTask suggestDestinationTask;

    /**
     * Private no-op constructor to prevent instantiation.
     */
    private SuggestionFactory() {
        throw new UnsupportedOperationException("Do not use an instance of this class, instead use the factory.");
    }

    /**
     * If there is a suggestion task in progress, kill it. Used to abort current suggestions if they are in progress.
     */
    public static void killSuggestDestinationTask() {
        if (suggestDestinationTask != null && suggestDestinationTask.getStatus() == AsyncTask.Status.RUNNING) {
            suggestDestinationTask.cancel(true);
        }
    }

    /**
     * Gets the list of suggestions and automatically populates an ArrayAdapter with suggestions.
     * @param query The query to get suggestions for.
     * @param suggestionAdapter The adapter to place the suggestions into.
     */
    public static void suggest(final String query, final ArrayAdapter<Destination> suggestionAdapter) {
        killSuggestDestinationTask();
        suggestDestinationTask = new SuggestDestinationTask(suggestionAdapter);
        suggestDestinationTask.execute(query);
    }

    private static final class SuggestDestinationTask extends AsyncTask<String, Integer, List<Destination>> {

        private static final int NUMBER_OF_CITIES_TO_DISPLAY = 6;

        private final ArrayAdapter<Destination> suggestionAdapter;

        private SuggestDestinationTask(final ArrayAdapter<Destination> suggestionAdapter) {
            this.suggestionAdapter = suggestionAdapter;
        }

        @Override
        protected List<Destination> doInBackground(final String... strings) {
            try {
                return RequestProcessor.run(new DestinationRequest(strings[0]));
            } catch (EanWsError ewe) {
                Log.d(SampleConstants.LOG_TAG, "The API call returned an error", ewe);
            } catch (UrlRedirectionException ure) {
                Log.d(SampleConstants.LOG_TAG, "The API call has been unexpectedly redirected!", ure);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<Destination> destinations) {
            if (destinations != null) {
                final List<Destination> cities = new ArrayList<Destination>();
                for (Destination destination : destinations) {
                    if (destination.category == Destination.Category.CITY) {
                        cities.add(destination);
                        if (cities.size() >= NUMBER_OF_CITIES_TO_DISPLAY) {
                            break;
                        }
                    }
                }
                suggestionAdapter.clear();
                suggestionAdapter.addAll(cities);
                suggestionAdapter.notifyDataSetChanged();
            }
        }
    }
}