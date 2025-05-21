package com.app.grocerybudgetplanner.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<GroceryItem> items;
    private double totalAmount;
    public Cart() {
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }
    public void addItem(GroceryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Item quantity must be positive");
        }
        for (GroceryItem cartItem : items) {
            if (cartItem.getId().equals(item.getId())) {
                int newQuantity = cartItem.getQuantity() + item.getQuantity();
                if (newQuantity <= 0) {
                    throw new IllegalArgumentException("Total quantity must be positive");
                }
                cartItem.setQuantity(newQuantity);
                updateTotalAmount();
                return;
            }
        }
        GroceryItem newItem = new GroceryItem(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getQuantity()
        );
        items.add(newItem);
        updateTotalAmount();
    }
    public void removeItem(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be null or empty");
        }
        items.removeIf(item -> item.getId().equals(itemId));
        updateTotalAmount();
    }
    public void updateItemQuantity(String itemId, int quantity) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        boolean found = false;
        for (GroceryItem item : items) {
            if (item.getId().equals(itemId)) {
                item.setQuantity(quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Item not found in cart");
        }
        updateTotalAmount();
    }
    private void updateTotalAmount() {
        totalAmount = 0.0;
        for (GroceryItem item : items) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
    }
    public List<GroceryItem> getItems() {
        return new ArrayList<>(items);
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void clear() {
        items.clear();
        totalAmount = 0.0;
    }
    public boolean isEmpty() {
        return items.isEmpty();
    }
    public int getItemCount() {
        return items.size();
    }
}