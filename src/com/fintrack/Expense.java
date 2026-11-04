package com.fintrack;

import java.time.LocalDate;

/**
 * Expense represents outgoing financial expenditures (e.g., Food, Rent, Utilities).
 * Demonstrates:
 * - Inheritance: Extends abstract base class Transaction.
 * - Polymorphism: Implements getType() returning "Expense" and getSignedAmount() returning negative amount.
 */
public class Expense extends Transaction {

    public static final String[] DEFAULT_CATEGORIES = {
            "Food & Dining",
            "Groceries",
            "Housing & Rent",
            "Utilities",
            "Transportation",
            "Shopping",
            "Entertainment",
            "Healthcare & Medical",
            "Education",
            "Personal Care",
            "Travel",
            "Bills & Fees",
            "Other Expense"
    };

    /**
     * Parameterized constructor calling superclass constructor.
     *
     * @param id          Unique transaction identifier
     * @param amount      Expense amount (> 0)
     * @param category    Expense category
     * @param description Brief description
     * @param date        Transaction date
     */
    public Expense(String id, double amount, String category, String description, LocalDate date) {
        super(id, amount, category, description, date);
    }

    @Override
    public String getType() {
        return "Expense";
    }

    @Override
    public double getSignedAmount() {
        return -getAmount();
    }
}

