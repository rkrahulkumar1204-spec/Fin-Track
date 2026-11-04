package com.fintrack;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Transaction is an abstract base class representing a generic financial transaction.
 * Demonstrates:
 * - Abstraction: Abstract class defining the contract for financial transactions.
 * - Encapsulation: Private member variables with validated getters and setters.
 * - Polymorphism: Abstract methods getType() and getSignedAmount() implemented by subclasses.
 */
public abstract class Transaction {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String id;
    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    /**
     * Parameterized constructor initializing a transaction with validation.
     *
     * @param id          Unique transaction identifier (e.g. TXN-1001)
     * @param amount      Transaction amount (must be > 0)
     * @param category    Transaction category
     * @param description Brief description or note
     * @param date        Date of transaction
     */
    public Transaction(String id, double amount, String category, String description, LocalDate date) {
        setId(id);
        setAmount(amount);
        setCategory(category);
        setDescription(description);
        setDate(date);
    }

    // ==================== Abstract Methods (Polymorphism) ====================

    /**
     * Returns the type of transaction (e.g., "Income" or "Expense").
     *
     * @return String representing transaction type
     */
    public abstract String getType();

    /**
     * Returns signed amount: positive for income, negative for expense.
     * Useful for balance calculations.
     *
     * @return signed double value
     */
    public abstract double getSignedAmount();

    // ==================== Getters and Setters (Encapsulation) ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        // Round to 2 decimal places to prevent floating-point inaccuracies
        this.amount = Math.round(amount * 100.0) / 100.0;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.category = category.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        this.description = description.trim();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.date = date;
    }

    // ==================== Helper Methods ====================

    /**
     * Formats the amount with currency symbol and 2 decimal places.
     */
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    /**
     * Formats the date using the standard format yyyy-MM-dd.
     */
    public String getFormattedDate() {
        return date.format(DATE_FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Date: %s | Amount: %s | Category: %s | Desc: %s",
                getType(), id, getFormattedDate(), getFormattedAmount(), category, description);
    }
}

