package com.app.grocerybudgetplanner.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private String month;
    private int year;
    private double amount;
    private double spent;
    public Budget() {
        this.id = "";
        this.userId = "";
        this.month = "";
        this.year = 0;
        this.amount = 0.0;
        this.spent = 0.0;
    }
    public Budget(String userId, String month, int year, double amount) {
        this.userId = userId;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.spent = 0.0;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Budget amount cannot be negative");
        }
        if (spent > amount) {
            throw new IllegalArgumentException("New budget amount (" + amount + 
                ") must be greater than or equal to spent amount (" + spent + ")");
        }
        this.amount = amount;
    }
    public double getSpent() {
        return spent;
    }
    public void setSpent(double spent) {
        if (spent < 0) {
            throw new IllegalArgumentException("Spent amount cannot be negative");
        }
        if (spent > amount) {
            throw new IllegalArgumentException("Spent amount (" + spent + 
                ") cannot exceed budget amount (" + amount + ")");
        }
        this.spent = spent;
    }
    public double getRemaining() {
        return Math.max(0, amount - spent);
    }
    public double getPercentageUsed() {
        if (amount == 0) return 0;
        double percentage = (spent / amount) * 100;
        return Math.min(percentage, 100);
    }
    public void setRemaining(double remaining) {
        if (remaining < 0) {
            throw new IllegalArgumentException("Remaining amount cannot be negative");
        }
        if (remaining > amount) {
            throw new IllegalArgumentException("Remaining amount cannot exceed budget amount");
        }
        this.spent = amount - remaining;
    }
    public void setPercentageUsed(int percentUsed) {
        if (percentUsed < 0 || percentUsed > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.spent = (amount * percentUsed) / 100;
    }
    public boolean canAfford(double expense) {
        return getRemaining() >= expense;
    }
    @Override
    public String toString() {
        return "Budget{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", month='" + month + '\'' +
                ", year=" + year +
                ", amount=" + amount +
                ", spent=" + spent +
                ", remaining=" + getRemaining() +
                ", percentUsed=" + getPercentageUsed() +
                '}';
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("userId", userId);
        result.put("month", month);
        result.put("year", year);
        result.put("amount", amount);
        result.put("spent", spent);
        return result;
    }
}