package com.app.grocerybudgetplanner.model;

import junit.framework.TestCase;

public class BudgetTest extends TestCase {
    private Budget budget;
    public void setUp() throws Exception {
        super.setUp();
        budget = new Budget("user123", "May", 2025, 500.0);
    }
    public void tearDown() throws Exception {
        budget = null;
    }
    public void testGetIdAndSetId() {
        budget.setId("budget001");
        assertEquals("budget001", budget.getId());
    }
    public void testGetUserIdAndSetUserId() {
        assertEquals("user123", budget.getUserId());
        budget.setUserId("newUser");
        assertEquals("newUser", budget.getUserId());
    }
    public void testGetMonthAndSetMonth() {
        assertEquals("May", budget.getMonth());
        budget.setMonth("June");
        assertEquals("June", budget.getMonth());
    }
    public void testGetYearAndSetYear() {
        assertEquals(2025, budget.getYear());
        budget.setYear(2026);
        assertEquals(2026, budget.getYear());
    }
    public void testGetAmountAndSetAmount() {
        assertEquals(500.0, budget.getAmount());
        budget.setAmount(600.0);
        assertEquals(600.0, budget.getAmount());
    }
    public void testSetAmountThrowsExceptionWhenLessThanSpent() {
        budget.setSpent(400.0);
        try {
            budget.setAmount(300.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("New budget amount (300.0) must be greater than or equal to spent amount (400.0)", e.getMessage());
        }
    }
    public void testGetSpentAndSetSpent() {
        budget.setSpent(150.0);
        assertEquals(150.0, budget.getSpent());
    }
    public void testSetSpentThrowsExceptionWhenNegative() {
        try {
            budget.setSpent(-10.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Spent amount cannot be negative", e.getMessage());
        }
    }
    public void testSetSpentThrowsExceptionWhenExceedsAmount() {
        try {
            budget.setSpent(600.0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Spent amount (600.0) cannot exceed budget amount (500.0)", e.getMessage());
        }
    }
    public void testGetRemaining() {
        budget.setSpent(200.0);
        assertEquals(300.0, budget.getRemaining());
    }
    public void testGetPercentageUsed() {
        budget.setSpent(250.0);
        assertEquals(50.0, budget.getPercentageUsed());
    }
    public void testSetRemaining() {
        budget.setRemaining(400.0);
        assertEquals(100.0, budget.getSpent());
    }
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
    public void testSetPercentageUsed() {
        budget.setPercentageUsed(20);
        assertEquals(100.0, budget.getSpent());
    }
    public void testSetPercentageUsedThrowsExceptionWhenOutOfRange() {
        try {
            budget.setPercentageUsed(110);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Percentage must be between 0 and 100", e.getMessage());
        }
    }
    public void testCanAfford() {
        budget.setSpent(450.0);
        assertTrue(budget.canAfford(50.0));
        assertFalse(budget.canAfford(60.0));
    }
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
