package com.fintrack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler provides reliable file input and output operations for transactions.
 * Persists data to a local CSV file and loads it on startup.
 * Handles proper CSV quoting and parsing for descriptions containing commas or quotes.
 */
public class FileHandler {

    public static final String DEFAULT_FILE_PATH = "data/transactions.csv";
    private static final String CSV_HEADER = "id,type,amount,category,description,date";

    /**
     * Loads all transactions from the specified CSV file.
     * If the file or directory does not exist, it creates them with a standard header.
     *
     * @param filePath Path to the CSV file
     * @return ArrayList of Transaction objects
     */
    public static ArrayList<Transaction> loadTransactions(String filePath) {
        ArrayList<Transaction> list = new ArrayList<>();
        File file = new File(filePath);

        // If file doesn't exist, ensure directories exist and initialize with header
        if (!file.exists()) {
            ensureDirectoryAndHeader(file);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                // Skip header line if present
                if (isFirstLine) {
                    isFirstLine = false;
                    if (trimmed.toLowerCase().startsWith("id,type")) {
                        continue;
                    }
                }

                try {
                    List<String> fields = parseCsvLine(trimmed);
                    if (fields.size() < 6) {
                        System.err.println("FileHandler: Skipping invalid line " + lineNumber + " (expected 6 columns, got " + fields.size() + "): " + line);
                        continue;
                    }

                    String id = fields.get(0).trim();
                    String type = fields.get(1).trim();
                    double amount = Double.parseDouble(fields.get(2).trim());
                    String category = fields.get(3).trim();
                    String description = fields.get(4).trim();
                    LocalDate date = LocalDate.parse(fields.get(5).trim(), Transaction.DATE_FORMATTER);

                    Transaction txn;
                    if ("Income".equalsIgnoreCase(type)) {
                        txn = new Income(id, amount, category, description, date);
                    } else if ("Expense".equalsIgnoreCase(type)) {
                        txn = new Expense(id, amount, category, description, date);
                    } else {
                        System.err.println("FileHandler: Unknown transaction type at line " + lineNumber + ": " + type);
                        continue;
                    }

                    list.add(txn);
                } catch (DateTimeParseException | IllegalArgumentException ex) {
                    System.err.println("FileHandler: Error parsing line " + lineNumber + " [" + ex.getMessage() + "]: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("FileHandler: Error reading from file " + filePath + ": " + e.getMessage());
        }

        return list;
    }

    /**
     * Saves all transactions into the specified CSV file.
     *
     * @param filePath     Path to the CSV file
     * @param transactions ArrayList of Transaction objects
     * @return true if save was successful, false otherwise
     */
    public static boolean saveTransactions(String filePath, ArrayList<Transaction> transactions) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {

            // Write CSV header
            writer.write(CSV_HEADER);
            writer.newLine();

            // Write each transaction
            for (Transaction t : transactions) {
                String row = String.format("%s,%s,%.2f,%s,%s,%s",
                        escapeCsv(t.getId()),
                        escapeCsv(t.getType()),
                        t.getAmount(),
                        escapeCsv(t.getCategory()),
                        escapeCsv(t.getDescription()),
                        t.getFormattedDate());
                writer.write(row);
                writer.newLine();
            }

            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("FileHandler: Error writing to file " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Ensures parent folder and initial empty CSV file with header exists.
     */
    private static void ensureDirectoryAndHeader(File file) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (file.createNewFile()) {
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    writer.write(CSV_HEADER);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("FileHandler: Could not initialize file: " + e.getMessage());
        }
    }

    /**
     * Escapes a field for CSV output: wraps in quotes if it contains commas, quotes, or newlines.
     */
    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Parses a single CSV line into tokens, properly handling quotes and commas within quotes.
     */
    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                // If next character is also a quote, it's an escaped quote
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());

        return tokens;
    }
}
