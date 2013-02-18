/*
 * Copyright 2013 EAN.com, L.P. All rights reserved.
 */
package com.ean.mobile.hotel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SupplierTypeTest {

    @Test
    public void testGetByIdWithBadIds() {
        assertNull(SupplierType.getById(0));
        assertNull(SupplierType.getById(50));
        assertNull(SupplierType.getById(7));
        assertNull(SupplierType.getById(Integer.MAX_VALUE));
        assertNull(SupplierType.getById(Integer.MIN_VALUE));
    }

    @Test
    public void testGetByIdWithExpediaIds() {
        assertEquals(SupplierType.EXPEDIA, SupplierType.getById(2));
        assertEquals(SupplierType.EXPEDIA, SupplierType.getById(9));
        assertEquals(SupplierType.EXPEDIA, SupplierType.getById(13));
    }

    @Test
    public void testGetByIdWithSabreId() {
        assertEquals(SupplierType.SABRE, SupplierType.getById(3));
    }

    @Test
    public void testGetByIdWithVenereId() {
        assertEquals(SupplierType.VENERE, SupplierType.getById(14));
    }

    @Test
    public void testGetByIdWithWorldspanId() {
        assertEquals(SupplierType.WORLDSPAN, SupplierType.getById(10));
    }

    @Test
    public void testToString() {
        assertEquals("EXPEDIA [E]", SupplierType.EXPEDIA.toString());
        assertEquals("VENERE [V]", SupplierType.VENERE.toString());
    }
}
