package com.ean.mobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RateInfo extends ArrayList<NightlyRate> {

    public String currencyCode;

    public boolean promo = false;

    public final Map<String, BigDecimal> surcharges;

    public RateInfo(JSONObject rateInfoJson) throws  JSONException {
        JSONObject chargeable = rateInfoJson.getJSONObject("ChargeableRateInfo");
        this.promo = rateInfoJson.getBoolean("@promo");
//        this.nightlyRateTotal = new BigDecimal(chargeable.getString("@nightlyRateTotal"));
//        this.averageBaseRate = new BigDecimal(chargeable.getString("@averageBaseRate"));
//        this.averageRate = new BigDecimal(chargeable.getString("@averageRate"));
        this.currencyCode = chargeable.getString("@currencyCode");
        if (chargeable.optJSONArray("NightlyRatesPerRoom") != null) {
            this.addAll(NightlyRate.parseNightlyRates(chargeable.getJSONArray("NightlyRatesPerRoom")));
        } else if(chargeable.optJSONObject("NightlyRatesPerRoom") != null) {
            if(chargeable.getJSONObject("NightlyRatesPerRoom").optJSONArray("NightlyRate") != null) {
            this.addAll(NightlyRate.parseNightlyRates(chargeable.getJSONObject("NightlyRatesPerRoom").getJSONArray("NightlyRate")));
            } else {
                this.addAll(NightlyRate.parseNightlyRates(chargeable.getJSONObject("NightlyRatesPerRoom")));
            }
        }

        Object surchargesJson = null;
        if ((surchargesJson = chargeable.optJSONArray("Surcharges")) != null) {
            surcharges = new HashMap<String, BigDecimal>(((JSONArray) surchargesJson).length());
            for (int i = 0; i < ((JSONArray) surchargesJson).length(); i++) {
                JSONObject surchargeJson = ((JSONArray) surchargesJson).getJSONObject(i);
                surcharges.put(surchargeJson.getString("@type"),
                               new BigDecimal(surchargeJson.getString("@amount")));
            }
        } else if ((surchargesJson = chargeable.optJSONObject("Surcharge")) != null) {
            surcharges = new HashMap<String, BigDecimal>(1);
            surcharges.put(((JSONObject) surchargesJson).getString("@type"),
                           new BigDecimal(((JSONObject) surchargesJson).getString("@amount")));
        } else {
            surcharges = new HashMap<String, BigDecimal>(0);
        }
    }

    public static ArrayList<RateInfo> parseRateInfos(JSONArray rateInfosJson) throws JSONException {
        ArrayList<RateInfo> rateInfos = new ArrayList<RateInfo>();
       // Log.d(EANMobileConstants.DEBUG_TAG, "parsing room rate infos");
        for(int j=0; j < rateInfosJson.length(); j++) {
            rateInfos.add(new RateInfo(rateInfosJson.getJSONObject(j)));
        }
       // Log.d(EANMobileConstants.DEBUG_TAG, "done parsing room rate infos");
        return rateInfos;
    }

    public static ArrayList<RateInfo> parseRateInfos(JSONObject rateInfosJson) throws JSONException {
        ArrayList<RateInfo> rateInfos = new ArrayList<RateInfo>();
       // Log.d(EANMobileConstants.DEBUG_TAG, "parsing single room rate info");
        rateInfos.add(new RateInfo(rateInfosJson.getJSONObject("RateInfo")));
       // Log.d(EANMobileConstants.DEBUG_TAG, "done parsing single room rate info");
        return rateInfos;
    }

    public BigDecimal getAverageRate() {
        BigDecimal avgRate = BigDecimal.ZERO;
        if (this.size() == 0) {
            return avgRate;
        }
        for (NightlyRate rate : this) {
            avgRate = avgRate.add(rate.rate);
        }
        avgRate = avgRate.divide(new BigDecimal(this.size()));
        return avgRate;
    }

    public BigDecimal getAverageBaseRate() {
        BigDecimal avgBaseRate = BigDecimal.ZERO;
        if (this.size() == 0) {
            return avgBaseRate;
        }
        for (NightlyRate rate : this) {
            avgBaseRate = avgBaseRate.add(rate.baseRate);
        }
        avgBaseRate = avgBaseRate.divide(new BigDecimal(this.size()));
        return avgBaseRate;
    }

    public boolean areAverageRatesEqual() {
        return this.getAverageRate().equals(this.getAverageBaseRate());
    }

    public BigDecimal getRateTotal() {
        BigDecimal outRate = BigDecimal.ZERO;
        for (NightlyRate rate : this) {
            outRate = outRate.add(rate.rate);
        }
        return outRate;
    }

    public BigDecimal getBaseRateTotal() {
        BigDecimal outRate = BigDecimal.ZERO;
        for (NightlyRate rate : this) {
            outRate = outRate.add(rate.baseRate);
        }
        return outRate;
    }
}
