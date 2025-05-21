package com.app.grocerybudgetplanner.model;

import junit.framework.TestCase;

public class GroceryItemTest extends TestCase {
    private GroceryItem item;

    // Runs before each test
    protected void setUp() throws Exception {
        super.setUp();
        item = new GroceryItem("1", "Apples", 1.5, 4);
    }

    public void testGetId() {
        assertEquals("1", item.getId());
    }

    public void testSetId() {
        item.setId("2");
        assertEquals("2", item.getId());
    }

    public void testGetName() {
        assertEquals("Apples", item.getName());
    }

    public void testSetName() {
        item.setName("Bananas");
        assertEquals("Bananas", item.getName());
    }

    public void testGetPrice() {
        assertEquals(1.5, item.getPrice(), 0.001);
    }

    public void testSetPriceNegative() {
        try {
            item.setPrice(-5);
            fail("Expected IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testSetPriceValid() {
        item.setPrice(2.0);
        assertEquals(2.0, item.getPrice(), 0.001);
    }

    public void testGetQuantity() {
        assertEquals(4, item.getQuantity());
    }

    public void testSetQuantityZero() {
        try {
            item.setQuantity(0);
            fail("Expected IllegalArgumentException for quantity 0");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testSetQuantityValid() {
        item.setQuantity(10);
        assertEquals(10, item.getQuantity());
    }

    public void testGetTotalCost() {
        assertEquals(6.0, item.getTotalCost(), 0.001);
    }

    public void testToStringOutput() {
        String output = item.toString();
        assertTrue(output.contains("Apples"));
        assertTrue(output.contains("6.0"));
    }
}
