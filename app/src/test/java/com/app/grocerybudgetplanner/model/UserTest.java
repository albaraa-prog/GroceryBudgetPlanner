package com.app.grocerybudgetplanner.model;

import junit.framework.TestCase;
import java.util.Map;

public class UserTest extends TestCase {

    private User user;
    private Budget budget;
    // Runs before each test — initializes User and Budget objects
    public void setUp() throws Exception {
        super.setUp();
        user = new User("john_doe", "john@example.com", "user");
        budget = new Budget("1", "January", 2025, 300.0);
    }
    // Cleans up after each test — sets objects to null
    public void tearDown() throws Exception {
        user = null;
        budget = null;
    }
    // Test adding a budget to the user and verifying it's stored correctly
    public void testAddBudget() {
        user.addBudget(budget);
        String key = "January_2025";
        assertTrue(user.getBudgets().containsKey(key));
        assertEquals(budget, user.getBudgets().get(key));
    }
    // Test retrieving a budget by month and year
    public void testGetBudget() {
        user.addBudget(budget);
        Budget retrieved = user.getBudget("January", 2025);
        assertNotNull(retrieved);
        assertEquals("January", retrieved.getMonth());
        assertEquals(2025, retrieved.getYear());
        assertEquals(300.0, retrieved.getAmount());
    }
    // Test checking if the user has a budget for a specific month and year
    public void testHasBudget() {
        assertFalse(user.hasBudget("January", 2025)); // Should be false initially
        user.addBudget(budget);
        assertTrue(user.hasBudget("January", 2025));  // Should be true after adding
    }
    // Test that the user's budget map contains correct key-value after addition
    public void testGetBudgets() {
        user.addBudget(budget);
        Map<String, Budget> budgets = user.getBudgets();
        assertEquals(1, budgets.size());
        assertTrue(budgets.containsKey("January_2025"));
    }
}
