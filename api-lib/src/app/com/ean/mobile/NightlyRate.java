package com.ean.mobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class NightlyRate {

    public boolean promo;

    public BigDecimal rate, baseRate;
    

    public NightlyRate(JSONObject nightlyRateJson) throws JSONException{
        this.promo = nightlyRateJson.getBoolean("@promo");
        this.rate = new BigDecimal(nightlyRateJson.getString("@rate"));
        this.baseRate = new BigDecimal(nightlyRateJson.getString("@baseRate"));
    }

    public static ArrayList<NightlyRate> parseNightlyRates(JSONArray nightlyRatesJson) throws JSONException {
        ArrayList<NightlyRate> nightlyRates = new ArrayList<NightlyRate>();
       // Log.d(EANMobileConstants.DEBUG_TAG, "parsing nightly rate details");
        for(int j=0; j < nightlyRatesJson.length(); j++) {
            nightlyRates.add(new NightlyRate(nightlyRatesJson.getJSONObject(j)));
        }
        //Log.d(EANMobileConstants.DEBUG_TAG, "done parsing nightly rate details");
        return nightlyRates;
    }

    public static ArrayList<NightlyRate> parseNightlyRates(JSONObject nightlyRatesJson) throws JSONException {
        ArrayList<NightlyRate> nightlyRates = new ArrayList<NightlyRate>();
        //Log.d(EANMobileConstants.DEBUG_TAG, "parsing single nightly rate detail");
        nightlyRates.add(new NightlyRate(nightlyRatesJson.getJSONObject("NightlyRate")));
        //Log.d(EANMobileConstants.DEBUG_TAG, "done parsing single nightly rate detail");
        return nightlyRates;
    }
}

