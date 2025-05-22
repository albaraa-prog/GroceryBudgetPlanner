package com.app.grocerybudgetplanner.model;

import junit.framework.TestCase;

public class BudgetTest extends TestCase {
    private Budget budget;
    // Setup method called before each test
    public void setUp() throws Exception {
        super.setUp();
        budget = new Budget("user123", "May", 2025, 500.0);
    }
    // Teardown method called after each test
    public void tearDown() throws Exception {
        budget = null;
    }
    // Test setting and getting the ID
    public void testGetIdAndSetId() {
        budget.setId("budget001");
        assertEquals("budget001", budget.getId());
    }
    // Test setting and getting the user ID
    public void testGetUserIdAndSetUserId() {
        assertEquals("user123", budget.getUserId());
        budget.setUserId("newUser");
        assertEquals("newUser", budget.getUserId());
    }
    // Test setting and getting the month
    public void testGetMonthAndSetMonth() {
        assertEquals("May", budget.getMonth());
        budget.setMonth("June");
        assertEquals("June", budget.getMonth());
    }
    // Test setting and getting the year
    public void testGetYearAndSetYear() {
        assertEquals(2025, budget.getYear());
        budget.setYear(2026);
        assertEquals(2026, budget.getYear());
    }
    // Test setting and getting the budget amount
    public void testGetAmountAndSetAmount() {
        assertEquals(500.0, budget.getAmount());
        budget.setAmount(600.0);
        assertEquals(600.0, budget.getAmount());
    }
    // Ensure setting an amount lower than the spent amount throws an exception
    public void testSetAmountThrowsExceptionWhenLessThanSpent() {
        budget.setSpent(400.0);
        try {
            budget.setAmount(300.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("New budget amount (300.0) must be greater than or equal to spent amount (400.0)", e.getMessage());
        }
    }
    // Test setting and getting the spent amount
    public void testGetSpentAndSetSpent() {
        budget.setSpent(150.0);
        assertEquals(150.0, budget.getSpent());
    }
    // Ensure setting a negative spent amount throws an exception
    public void testSetSpentThrowsExceptionWhenNegative() {
        try {
            budget.setSpent(-10.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Spent amount cannot be negative", e.getMessage());
        }
    }
    // Ensure setting spent more than the budget amount throws an exception
    public void testSetSpentThrowsExceptionWhenExceedsAmount() {
        try {
            budget.setSpent(600.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Spent amount (600.0) cannot exceed budget amount (500.0)", e.getMessage());
        }
    }
    // Test calculation of remaining amount
    public void testGetRemaining() {
        budget.setSpent(200.0);
        assertEquals(300.0, budget.getRemaining());
    }
    // Test calculation of percentage used
    public void testGetPercentageUsed() {
        budget.setSpent(250.0);
        assertEquals(50.0, budget.getPercentageUsed());
    }
    // Test setting remaining amount updates spent correctly
    public void testSetRemaining() {
        budget.setRemaining(400.0);
        assertEquals(100.0, budget.getSpent());
    }
    // Ensure setting invalid remaining amounts throws appropriate exceptions
    public void testSetRemainingThrowsExceptionWhenInvalid() {
        try {
            budget.setRemaining(-1.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Remaining amount cannot be negative", e.getMessage());
        }
        try {
            budget.setRemaining(600.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Remaining amount cannot exceed budget amount", e.getMessage());
        }
    }
    // Test setting percentage used correctly updates spent
    public void testSetPercentageUsed() {
        budget.setPercentageUsed(20);
        assertEquals(100.0, budget.getSpent());
    }
    // Ensure setting invalid percentage used throws an exception
    public void testSetPercentageUsedThrowsExceptionWhenOutOfRange() {
        try {
            budget.setPercentageUsed(110);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Percentage must be between 0 and 100", e.getMessage());
        }
    }
    // Test if the user can afford a new expense based on remaining budget
    public void testCanAfford() {
        budget.setSpent(450.0);
        assertTrue(budget.canAfford(50.0));  // Exactly remaining
        assertFalse(budget.canAfford(60.0)); // Exceeds remaining
    }
    // Test the toString method to ensure it includes key values
    public void testToStringMethod() {
        budget.setId("budget001");
        budget.setSpent(100.0);
        String result = budget.toString();
        assertTrue(result.contains("id='budget001'"));
        assertTrue(result.contains("userId='user123'"));
        assertTrue(result.contains("month='May'"));
        assertTrue(result.contains("year=2025"));
        assertTrue(result.contains("amount=500.0"));
        assertTrue(result.contains("spent=100.0"));
    }
    // Test conversion of the budget object to a map representation
    public void testToMap() {
        budget.setId("b1");
        budget.setSpent(100.0);
        assertEquals("b1", budget.toMap().get("id"));
        assertEquals("user123", budget.toMap().get("userId"));
        assertEquals("May", budget.toMap().get("month"));
        assertEquals(2025, budget.toMap().get("year"));
        assertEquals(500.0, budget.toMap().get("amount"));
        assertEquals(100.0, budget.toMap().get("spent"));
    }
}
