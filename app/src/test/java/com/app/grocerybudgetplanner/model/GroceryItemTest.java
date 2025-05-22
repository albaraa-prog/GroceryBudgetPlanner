package com.app.grocerybudgetplanner.model;

import junit.framework.TestCase;

public class GroceryItemTest extends TestCase {
    private GroceryItem item;
    // Initializes a GroceryItem object before each test
    protected void setUp() throws Exception {
        super.setUp();
        item = new GroceryItem("1", "Apples", 1.5, 4); // Total cost = 6.0
    }
    // Test getting the ID of the item
    public void testGetId() {
        assertEquals("1", item.getId());
    }
    // Test setting a new ID for the item
    public void testSetId() {
        item.setId("2");
        assertEquals("2", item.getId());
    }
    // Test getting the name of the item
    public void testGetName() {
        assertEquals("Apples", item.getName());
    }
    // Test setting a new name for the item
    public void testSetName() {
        item.setName("Bananas");
        assertEquals("Bananas", item.getName());
    }
    // Test getting the price of the item
    public void testGetPrice() {
        assertEquals(1.5, item.getPrice(), 0.001);
    }
    // Test setting a negative price throws an exception
    public void testSetPriceNegative() {
        try {
            item.setPrice(-5);
            fail("Expected IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }
    // Test setting a valid price updates the item correctly
    public void testSetPriceValid() {
        item.setPrice(2.0);
        assertEquals(2.0, item.getPrice(), 0.001);
    }
    // Test getting the quantity of the item
    public void testGetQuantity() {
        assertEquals(4, item.getQuantity());
    }
    // Test setting quantity to zero throws an exception
    public void testSetQuantityZero() {
        try {
            item.setQuantity(0);
            fail("Expected IllegalArgumentException for quantity 0");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }
    // Test setting a valid quantity updates the item correctly
    public void testSetQuantityValid() {
        item.setQuantity(10);
        assertEquals(10, item.getQuantity());
    }
    // Test that the total cost is calculated correctly (price * quantity)
    public void testGetTotalCost() {
        assertEquals(6.0, item.getTotalCost(), 0.001);
    }
    // Test that toString() contains item details like name and total cost
    public void testToStringOutput() {
        String output = item.toString();
        assertTrue(output.contains("Apples"));
        assertTrue(output.contains("6.0"));
    }
}
