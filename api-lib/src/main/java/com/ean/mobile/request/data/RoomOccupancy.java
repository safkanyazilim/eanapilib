package com.ean.mobile.request.data;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.omg.DynamicAny.NameValuePairSeqHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container for room occupancy.
 */
public final class RoomOccupancy {

    public final int numberOfAdults;

    public final List<Integer> childAges;

    public RoomOccupancy(final int numberOfAdults, final List<Integer> childAges) {
        this.numberOfAdults = numberOfAdults;
        this.childAges = childAges == null ? Collections.<Integer>emptyList() : Collections.unmodifiableList(childAges);
    }

    public String asRequestString() {
        StringBuilder adultsAndChildren = new StringBuilder((childAges.size() * 2) + 1);
        adultsAndChildren.append(numberOfAdults);
        for (int childAge : childAges) {
            adultsAndChildren.append(",");
            adultsAndChildren.append(childAge);
        }
        return adultsAndChildren.toString();
    }

    public static List<NameValuePair> asNameValuePairs(List<RoomOccupancy> occupancies) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>(occupancies.size());
        for (int i = 1; i <= occupancies.size(); i++) {
            pairs.add(new BasicNameValuePair("room" + i, occupancies.get(i).asRequestString()));
        }
        return pairs;
    }

}
