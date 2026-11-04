package com.fintrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FinanceManager acts as the central business logic controller.
 * It manages the collection of transactions, balance calculations,
 * filtering, searching, unique ID generation, and disk persistence.
 */
public class FinanceManager {

    private final ArrayList<Transaction> transactions;
    private final String filePath;
    private static final Pattern TXN_ID_PATTERN = Pattern.compile("^TXN-(\\d+)$", Pattern.CASE_INSENSITIVE);

    /**
     * Default constructor using standard storage file path.
     */
    public FinanceManager() {
        this(FileHandler.DEFAULT_FILE_PATH);
    }

    /**
     * Constructor allowing custom file path.
     *
     * @param filePath Path to the storage CSV file
     */
    public FinanceManager(String filePath) {
        this.filePath = filePath;
        this.transactions = new ArrayList<>();
        loadData();
    }

    /**
     * Loads saved transactions from file into memory.
     */
    public void loadData() {
        transactions.clear();
        ArrayList<Transaction> loaded = FileHandler.loadTransactions(filePath);
        if (loaded != null) {
            transactions.addAll(loaded);
        }
    }

    /**
     * Persists current transactions to file.
     *
     * @return true if successful
     */
    public boolean saveData() {
        return FileHandler.saveTransactions(filePath, transactions);
    }

    /**
     * Adds a new transaction and automatically saves to file.
     *
     * @param transaction Transaction object to add
     * @return true if added successfully
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        transactions.add(transaction);
        return saveData();
    }

    /**
     * Deletes a transaction by its unique ID and updates the file.
     *
     * @param id Transaction ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteTransaction(String id) {
        if (id == null) return false;
        boolean removed = transactions.removeIf(t -> t.getId().equalsIgnoreCase(id.trim()));
        if (removed) {
            saveData();
        }
        return removed;
    }

    /**
     * Deletes multiple transactions by their IDs.
     *
     * @param ids List of transaction IDs to delete
     * @return count of deleted transactions
     */
    public int deleteTransactions(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        int countBefore = transactions.size();
        transactions.removeIf(t -> ids.contains(t.getId()));
        int deletedCount = countBefore - transactions.size();
        if (deletedCount > 0) {
            saveData();
        }
        return deletedCount;
    }

    /**
     * Returns a defensive copy of all transactions.
     *
     * @return ArrayList of transactions
     */
    public ArrayList<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Returns all transactions sorted by date descending (most recent first).
     */
    public ArrayList<Transaction> getRecentTransactions(int limit) {
        ArrayList<Transaction> copy = new ArrayList<>(transactions);
        copy.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        if (limit > 0 && copy.size() > limit) {
            return new ArrayList<>(copy.subList(0, limit));
        }
        return copy;
    }

    /**
     * Finds a transaction by its ID.
     */
    public Transaction getTransactionById(String id) {
        if (id == null) return null;
        for (Transaction t : transactions) {
            if (t.getId().equalsIgnoreCase(id.trim())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Returns the total count of transactions.
     */
    public int getTransactionCount() {
        return transactions.size();
    }

    // ==================== Financial Calculations ====================

    /**
     * Calculates total income across all transactions.
     */
    public double getTotalIncome() {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Income) {
                total += t.getAmount();
            }
        }
        return round(total);
    }

    /**
     * Calculates total expenses across all transactions.
     */
    public double getTotalExpenses() {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Expense) {
                total += t.getAmount();
            }
        }
        return round(total);
    }

    /**
     * Calculates current balance: Total Income - Total Expenses.
     */
    public double getBalance() {
        return round(getTotalIncome() - getTotalExpenses());
    }

    /**
     * Calculates total income for a specific month and year.
     */
    public double getMonthlyIncome(int year, int month) {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Income && t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                total += t.getAmount();
            }
        }
        return round(total);
    }

    /**
     * Calculates total expenses for a specific month and year.
     */
    public double getMonthlyExpenses(int year, int month) {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t instanceof Expense && t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                total += t.getAmount();
            }
        }
        return round(total);
    }

    /**
     * Calculates net balance for a specific month and year.
     */
    public double getMonthlyBalance(int year, int month) {
        return round(getMonthlyIncome(year, month) - getMonthlyExpenses(year, month));
    }

    /**
     * Returns all transactions for a given month and year.
     */
    public ArrayList<Transaction> getTransactionsForMonth(int year, int month) {
        ArrayList<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                result.add(t);
            }
        }
        result.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        return result;
    }

    /**
     * Returns a breakdown of amounts grouped by category for a specific month and type.
     *
     * @param year       Year
     * @param month      Month (1-12)
     * @param typeFilter "Income", "Expense", or null for all
     * @return Map of category name to sum amount
     */
    public Map<String, Double> getCategoryBreakdown(int year, int month, String typeFilter) {
        Map<String, Double> breakdown = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                if (typeFilter == null || typeFilter.equalsIgnoreCase("ALL") || t.getType().equalsIgnoreCase(typeFilter)) {
                    double current = breakdown.getOrDefault(t.getCategory(), 0.0);
                    breakdown.put(t.getCategory(), round(current + t.getAmount()));
                }
            }
        }
        return breakdown;
    }

    // ==================== Search and Filter ====================

    /**
     * Searches transactions matching a keyword and type filter.
     *
     * @param query      Keyword to search (matches ID, category, description, date)
     * @param typeFilter "All", "Income", or "Expense"
     * @return Filtered list of transactions
     */
    public ArrayList<Transaction> searchTransactions(String query, String typeFilter) {
        ArrayList<Transaction> result = new ArrayList<>();
        String q = (query == null) ? "" : query.trim().toLowerCase();
        String type = (typeFilter == null) ? "ALL" : typeFilter.trim().toUpperCase();

        for (Transaction t : transactions) {
            // Type filter check
            if (!type.equals("ALL") && !type.equals("ALL TRANSACTIONS")) {
                if (!t.getType().equalsIgnoreCase(type)) {
                    continue;
                }
            }

            // Keyword filter check
            if (q.isEmpty()) {
                result.add(t);
            } else {
                boolean matches = t.getId().toLowerCase().contains(q)
                        || t.getCategory().toLowerCase().contains(q)
                        || t.getDescription().toLowerCase().contains(q)
                        || t.getFormattedDate().contains(q)
                        || t.getType().toLowerCase().contains(q)
                        || String.valueOf(t.getAmount()).contains(q);
                if (matches) {
                    result.add(t);
                }
            }
        }

        // Sort by date descending (newest first)
        result.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        return result;
    }

    // ==================== Unique ID Generation ====================

    /**
     * Generates a unique, non-colliding transaction ID formatted as "TXN-XXXX".
     * Finds the maximum existing numeric suffix and increments by 1.
     *
     * @return Unique ID string
     */
    public synchronized String generateNextId() {
        int maxId = 1000;
        for (Transaction t : transactions) {
            Matcher matcher = TXN_ID_PATTERN.matcher(t.getId());
            if (matcher.matches()) {
                try {
                    int val = Integer.parseInt(matcher.group(1));
                    if (val > maxId) {
                        maxId = val;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return "TXN-" + (maxId + 1);
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}

