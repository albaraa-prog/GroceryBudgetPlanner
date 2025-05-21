package com.app.grocerybudgetplanner.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String username, email, role;
    public Map<String, Budget> budgets;
    public User() {
        this.budgets = new HashMap<>();
    }
    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.budgets = new HashMap<>();
    }
    public void addBudget(Budget budget) {
        if (budget == null) return;
        String budgetKey = budget.getMonth() + "_" + budget.getYear();
        budgets.put(budgetKey, budget);
    }
    public Budget getBudget(String month, int year) {
        String budgetKey = month + "_" + year;
        return budgets.get(budgetKey);
    }
    public boolean hasBudget(String month, int year) {
        String budgetKey = month + "_" + year;
        return budgets.containsKey(budgetKey);
    }
    public Map<String, Budget> getBudgets() {
        return budgets;
    }
}
