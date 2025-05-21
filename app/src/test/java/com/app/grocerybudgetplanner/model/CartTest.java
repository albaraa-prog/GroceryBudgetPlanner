package com.app.grocerybudgetplanner.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class CartTest {

    private Cart cart;
    private GroceryItem item1;
    private GroceryItem item2;
    @Before
    public void setUp() {
        cart = new Cart();
        item1 = new GroceryItem("1", "Milk", 2.5, 2);
        item2 = new GroceryItem("2", "Bread", 1.5, 3);
    }
    @Test
    public void testAddItem() {
        cart.addItem(item1);
        assertEquals(1, cart.getItemCount());
        assertEquals(5.0, cart.getTotalAmount(), 0.001);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddNullItem() {
        cart.addItem(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddItemWithZeroQuantity() {
        cart.addItem(new GroceryItem("3", "Eggs", 1.2, 0));
    }
    @Test
    public void testRemoveItem() {
        cart.addItem(item1);
        cart.addItem(item2);
        cart.removeItem("1");
        assertEquals(1, cart.getItemCount());
        assertEquals(4.5, cart.getTotalAmount(), 0.001);
    }
    @Test
    public void testUpdateItemQuantity() {
        cart.addItem(item1);
        cart.updateItemQuantity("1", 5);
        assertEquals(12.5, cart.getTotalAmount(), 0.001);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateItemQuantityWithZero() {
        cart.addItem(item1);
        cart.updateItemQuantity("1", 0);
    }
    @Test
    public void testGetItemsReturnsCopy() {
        cart.addItem(item1);
        List<GroceryItem> original = cart.getItems();
        original.clear(); // Should not affect internal list
        assertEquals(1, cart.getItemCount());
    }
    @Test
    public void testGetTotalAmount() {
        cart.addItem(item1);
        cart.addItem(item2);
        assertEquals(9.5, cart.getTotalAmount(), 0.001);
    }
    @Test
    public void testClear() {
        cart.addItem(item1);
        cart.clear();
        assertEquals(0, cart.getItemCount());
        assertEquals(0.0, cart.getTotalAmount(), 0.001);
    }
    @Test
    public void testIsEmpty() {
        assertTrue(cart.isEmpty());
        cart.addItem(item1);
        assertFalse(cart.isEmpty());
    }
    @Test
    public void testGetItemCount() {
        assertEquals(0, cart.getItemCount());
        cart.addItem(item1);
        assertEquals(1, cart.getItemCount());
    }
}
