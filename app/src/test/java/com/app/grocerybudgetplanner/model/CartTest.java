package com.app.grocerybudgetplanner.model;

import junit.framework.TestCase;
import java.util.List;
public class CartTest extends TestCase {
    private Cart cart;
    private GroceryItem item1;
    private GroceryItem item2;
    // Setup test objects before each test
    public void setUp() {
        cart = new Cart();
        item1 = new GroceryItem("1", "Milk", 2.5, 2);
        item2 = new GroceryItem("2", "Bread", 1.5, 3);
    }
    // Test adding a valid item to the cart
    public void testAddItem() {
        cart.addItem(item1);
        assertEquals(1, cart.getItemCount()); // Check item count
        assertEquals(5.0, cart.getTotalAmount(), 0.001); // Check total price
    }
    // Test that adding a null item throws an exception
    public void testAddNullItem() {
        try {
            cart.addItem(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Confirm error message
        }
    }
    // Test that adding an item with zero quantity throws an exception
    public void testAddItemWithZeroQuantity() {
        try {
            cart.addItem(new GroceryItem("3", "Eggs", 1.2, 0));
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Confirm error message
        }
    }
    // Test removing an item by ID from the cart
    public void testRemoveItem() {
        cart.addItem(item1);
        cart.addItem(item2);
        cart.removeItem("1");
        assertEquals(1, cart.getItemCount()); // One item should remain
        assertEquals(4.5, cart.getTotalAmount(), 0.001); // Total should reflect remaining item
    }
    // Test updating the quantity of an existing item
    public void testUpdateItemQuantity() {
        cart.addItem(item1);
        cart.updateItemQuantity("1", 5); // Change quantity from 2 to 5
        assertEquals(12.5, cart.getTotalAmount(), 0.001); // Total = 2.5 * 5
    }
    // Test that updating an item's quantity to zero throws an exception
    public void testUpdateItemQuantityWithZero() {
        cart.addItem(item1);
        try {
            cart.updateItemQuantity("1", 0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Confirm error message
        }
    }
    // Test that getItems returns a copy and not the original list
    public void testGetItemsReturnsCopy() {
        cart.addItem(item1);
        List<GroceryItem> original = cart.getItems();
        original.clear(); // Modify the returned list
        assertEquals(1, cart.getItemCount()); // Internal cart list should remain unchanged
    }
    // Test total amount calculation for multiple items
    public void testGetTotalAmount() {
        cart.addItem(item1);
        cart.addItem(item2);
        assertEquals(9.5, cart.getTotalAmount(), 0.001); // 2.5*2 + 1.5*3
    }
    // Test clearing the cart
    public void testClear() {
        cart.addItem(item1);
        cart.clear(); // Clear all items
        assertEquals(0, cart.getItemCount());
        assertEquals(0.0, cart.getTotalAmount(), 0.001);
    }
    // Test isEmpty method before and after adding an item
    public void testIsEmpty() {
        assertTrue(cart.isEmpty()); // Cart should be empty initially
        cart.addItem(item1);
        assertFalse(cart.isEmpty()); // Not empty after adding an item
    }
    // Test item count accuracy
    public void testGetItemCount() {
        assertEquals(0, cart.getItemCount()); // Initially 0
        cart.addItem(item1);
        assertEquals(1, cart.getItemCount()); // Should increase to 1
    }
}
