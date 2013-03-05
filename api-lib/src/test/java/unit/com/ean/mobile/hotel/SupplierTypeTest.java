/*
 * Copyright (c) 2013, Expedia Affiliate Network
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that redistributions of source code
 * retain the above copyright notice, these conditions, and the following
 * disclaimer. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the Expedia Affiliate Network or Expedia Inc.
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
