package com.fintrack;

import javax.swing.*;
import java.awt.*;

/**
 * Main is the entry point for the FinTrack – Personal Finance Manager desktop application.
 * Initializes the FinanceManager and loads the modern Java Swing Dashboard on the Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Enable anti-aliasing for smooth and crisp text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Set native System Look and Feel for clean OS integration
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fall back gracefully to default Swing look and feel
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }

            // Global UI font customization
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));

            // Initialize business logic layer (loads data from local CSV)
            FinanceManager financeManager = new FinanceManager();

            // Initialize and present main GUI Dashboard
            Dashboard dashboard = new Dashboard(financeManager);
            dashboard.setVisible(true);
        });
    }
}

