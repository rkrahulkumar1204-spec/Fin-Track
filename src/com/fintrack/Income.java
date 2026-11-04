package com.fintrack;

import java.time.LocalDate;

/**
 * Income represents incoming financial flow (e.g., Salary, Business, Investment).
 * Demonstrates:
 * - Inheritance: Extends abstract base class Transaction.
 * - Polymorphism: Implements getType() returning "Income" and getSignedAmount() returning positive amount.
 */
public class Income extends Transaction {

    public static final String[] DEFAULT_CATEGORIES = {
            "Salary",
            "Business",
            "Freelance",
            "Investments",
            "Rental Income",
            "Grants & Gifts",
            "Refunds",
            "Interest",
            "Other Income"
    };

    /**
     * Parameterized constructor calling superclass constructor.
     *
     * @param id          Unique transaction identifier
     * @param amount      Income amount (> 0)
     * @param category    Income category
     * @param description Brief description
     * @param date        Transaction date
     */
    public Income(String id, double amount, String category, String description, LocalDate date) {
        super(id, amount, category, description, date);
    }

    @Override
    public String getType() {
        return "Income";
    }

    @Override
    public double getSignedAmount() {
        return getAmount();
    }
}

