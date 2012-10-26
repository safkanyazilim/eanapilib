/*
 * Copyright 2012 EAN.com, L.P. All rights reserved.
 */

package com.ean.mobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public final class RateInfo implements List<NightlyRate> {

    private final List<NightlyRate> nightlyRates = new ArrayList<NightlyRate>();

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

    public static List<RateInfo> parseRateInfos(JSONArray rateInfosJson) throws JSONException {
        ArrayList<RateInfo> rateInfos = new ArrayList<RateInfo>(rateInfosJson.length());
       // Log.d(EANMobileConstants.DEBUG_TAG, "parsing room rate infos");
        for(int j=0; j < rateInfosJson.length(); j++) {
            rateInfos.add(new RateInfo(rateInfosJson.getJSONObject(j)));
        }
       // Log.d(EANMobileConstants.DEBUG_TAG, "done parsing room rate infos");
        return rateInfos;
    }

    public static List<RateInfo> parseRateInfos(JSONObject rateInfosJson) throws JSONException {
        ArrayList<RateInfo> rateInfos = new ArrayList<RateInfo>(1);
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
        for (NightlyRate rate : nightlyRates) {
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
        for (NightlyRate rate : nightlyRates) {
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
        for (NightlyRate rate : nightlyRates) {
            outRate = outRate.add(rate.rate);
        }
        return outRate;
    }

    public BigDecimal getBaseRateTotal() {
        BigDecimal outRate = BigDecimal.ZERO;
        for (NightlyRate rate : nightlyRates) {
            outRate = outRate.add(rate.baseRate);
        }
        return outRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int i, NightlyRate nightlyRate) {
        nightlyRates.add(i, nightlyRate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(NightlyRate nightlyRate) {
        return nightlyRates.add(nightlyRate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int i, Collection<? extends NightlyRate> nightlyRates) {
        return this.nightlyRates.addAll(i, nightlyRates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends NightlyRate> nightlyRates) {
        return this.nightlyRates.addAll(nightlyRates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        nightlyRates.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return nightlyRates.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> objects) {
        return containsAll(objects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NightlyRate get(int i) {
        return nightlyRates.get(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return nightlyRates.indexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return nightlyRates.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<NightlyRate> iterator() {
        return nightlyRates.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return nightlyRates.lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<NightlyRate> listIterator() {
        return nightlyRates.listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<NightlyRate> listIterator(int i) {
        return nightlyRates.listIterator(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NightlyRate remove(int i) {
        return nightlyRates.remove(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return nightlyRates.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> objects) {
        return nightlyRates.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return retainAll(objects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NightlyRate set(int i, NightlyRate nightlyRate) {
        return nightlyRates.set(i, nightlyRate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return nightlyRates.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NightlyRate> subList(int i, int i1) {
        return nightlyRates.subList(i, i1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return nightlyRates.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] ts) {
        if (!(ts instanceof NightlyRate[])) {
            throw new IllegalArgumentException("Argument ts must be of type NightlyRate[]!");
        }
        return (T[]) nightlyRates.toArray((NightlyRate[]) ts);
    }
}
