package com.fintrack;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard is the primary Swing GUI for FinTrack.
 * Features:
 * - Modern sidebar navigation
 * - Dashboard Overview with stat cards, quick actions, and recent activity
 * - Add Income Form with full validation
 * - Add Expense Form with full validation
 * - Transaction History Table with live search, type filter, and deletion
 * - Monthly Financial Summary with category breakdown and monthly records
 */
public class Dashboard extends JFrame {

    // Color Palette
    private static final Color COLOR_SIDEBAR_BG = new Color(15, 23, 42);       // Slate 900: #0F172A
    private static final Color COLOR_SIDEBAR_HOVER = new Color(30, 41, 59);    // Slate 800: #1E293B
    private static final Color COLOR_SIDEBAR_ACTIVE = new Color(37, 99, 235);   // Blue 600: #2563EB
    private static final Color COLOR_SIDEBAR_TEXT = new Color(203, 213, 225);  // Slate 300: #CBD5E1 (high contrast on dark navy)

    private static final Color COLOR_CONTENT_BG = new Color(248, 250, 252);    // Slate 50
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(226, 232, 240);        // Slate 200
    private static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);        // Slate 900
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);    // Slate 500

    private static final Color COLOR_INCOME = new Color(5, 150, 105);          // Green 600: #059669
    private static final Color COLOR_INCOME_HOVER = new Color(4, 120, 87);     // Green 700: #047857
    private static final Color COLOR_INCOME_BG = new Color(236, 253, 245);     // Green 50: #ECFDF5

    private static final Color COLOR_EXPENSE = new Color(220, 38, 38);         // Red 600: #DC2626
    private static final Color COLOR_EXPENSE_HOVER = new Color(185, 28, 28);   // Red 700: #B91C1C
    private static final Color COLOR_EXPENSE_BG = new Color(254, 242, 242);    // Red 50: #FEF2F2

    private static final Color COLOR_NAVY_BTN = new Color(30, 41, 59);         // Slate 800: #1E293B
    private static final Color COLOR_NAVY_HOVER = new Color(15, 23, 42);       // Slate 900: #0F172A

    private static final Color COLOR_PRIMARY_BLUE = new Color(37, 99, 235);    // Blue 600: #2563EB
    private static final Color COLOR_PRIMARY_HOVER = new Color(29, 78, 216);   // Blue 700: #1D4ED8

    private static final Color COLOR_CLEAR_BG = new Color(241, 245, 249);      // Slate 100: #F1F5F9
    private static final Color COLOR_CLEAR_BORDER = new Color(203, 213, 225);  // Slate 300: #CBD5E1
    private static final Color COLOR_CLEAR_HOVER = new Color(226, 232, 240);   // Slate 200: #E2E8F0

    private static final Color COLOR_BALANCE = new Color(59, 130, 246);        // Blue 500
    private static final Color COLOR_BALANCE_BG = new Color(239, 246, 255);    // Blue 50
    private static final Color COLOR_COUNT = new Color(139, 92, 246);          // Violet 500
    private static final Color COLOR_COUNT_BG = new Color(245, 243, 255);      // Violet 50

    // Fonts
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 24);

    // Business Logic
    private final FinanceManager financeManager;

    // Navigation & Layout
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private final List<NavButton> navButtons = new ArrayList<>();
    private JLabel headerBalanceLabel;
    private JLabel headerDateLabel;

    // View Components: Dashboard Overview
    private StatCard cardTotalIncome;
    private StatCard cardTotalExpenses;
    private StatCard cardCurrentBalance;
    private StatCard cardTotalCount;
    private DefaultTableModel recentTableModel;
    private JLabel savingsHealthLabel;

    // View Components: Add Income Form
    private JTextField incomeAmountField;
    private JComboBox<String> incomeCategoryBox;
    private JTextField incomeDateField;
    private JTextField incomeDescField;
    private JLabel incomeMsgLabel;

    // View Components: Add Expense Form
    private JTextField expenseAmountField;
    private JComboBox<String> expenseCategoryBox;
    private JTextField expenseDateField;
    private JTextField expenseDescField;
    private JLabel expenseMsgLabel;

    // View Components: Transaction History
    private JTextField searchField;
    private JComboBox<String> filterTypeBox;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JLabel historyCountLabel;

    // View Components: Monthly Summary
    private JComboBox<String> summaryMonthBox;
    private JComboBox<Integer> summaryYearBox;
    private StatCard cardMonthlyIncome;
    private StatCard cardMonthlyExpenses;
    private StatCard cardMonthlyBalance;
    private DefaultTableModel monthlyCategoryTableModel;
    private DefaultTableModel monthlyTxnTableModel;
    private JLabel monthlySummaryTitle;

    public Dashboard(FinanceManager financeManager) {
        this.financeManager = financeManager;

        setTitle("FinTrack – Personal Finance Manager");
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        refreshAllViews();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_CONTENT_BG);

        // 1. Sidebar Navigation (West)
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // 2. Main Content Wrapper (Center)
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(COLOR_CONTENT_BG);

        // Top Header
        JPanel topHeader = createTopHeader();
        centerWrapper.add(topHeader, BorderLayout.NORTH);

        // Center Views (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(COLOR_CONTENT_BG);

        mainContentPanel.add(createDashboardOverviewPanel(), "DASHBOARD");
        mainContentPanel.add(createAddIncomePanel(), "ADD_INCOME");
        mainContentPanel.add(createAddExpensePanel(), "ADD_EXPENSE");
        mainContentPanel.add(createTransactionHistoryPanel(), "HISTORY");
        mainContentPanel.add(createMonthlySummaryPanel(), "MONTHLY");

        centerWrapper.add(mainContentPanel, BorderLayout.CENTER);
        root.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ==================== Sidebar Navigation ====================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 760));
        sidebar.setBackground(COLOR_SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(24, 16, 20, 16));

        // Brand Logo & Title
        JPanel brandPanel = new JPanel(new BorderLayout(10, 0));
        brandPanel.setOpaque(false);

        JLabel brandIcon = new JLabel("FT", SwingConstants.CENTER);
        brandIcon.setOpaque(true);
        brandIcon.setBackground(COLOR_SIDEBAR_ACTIVE);
        brandIcon.setForeground(Color.WHITE);
        brandIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandIcon.setPreferredSize(new Dimension(38, 38));
        brandPanel.add(brandIcon, BorderLayout.WEST);

        JPanel brandTextPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        brandTextPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("FinTrack");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel subtitleLabel = new JLabel("Finance Manager");
        subtitleLabel.setForeground(COLOR_SIDEBAR_TEXT);
        subtitleLabel.setFont(FONT_SMALL);

        brandTextPanel.add(titleLabel);
        brandTextPanel.add(subtitleLabel);
        brandPanel.add(brandTextPanel, BorderLayout.CENTER);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(32, 0, 0, 0));

        NavButton btnDashboard = new NavButton("  Dashboard", "DASHBOARD");
        NavButton btnAddIncome = new NavButton("+ Add Income", "ADD_INCOME");
        NavButton btnAddExpense = new NavButton("- Add Expense", "ADD_EXPENSE");
        NavButton btnHistory = new NavButton("  Transaction History", "HISTORY");
        NavButton btnMonthly = new NavButton("  Monthly Summary", "MONTHLY");

        navButtons.add(btnDashboard);
        navButtons.add(btnAddIncome);
        navButtons.add(btnAddExpense);
        navButtons.add(btnHistory);
        navButtons.add(btnMonthly);

        for (NavButton btn : navButtons) {
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(8));
        }

        // Highlight first by default
        btnDashboard.setActive(true);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Bottom Footer in Sidebar
        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        footerPanel.setOpaque(false);
        JLabel systemTag = new JLabel("Offline Core Java App");
        systemTag.setForeground(COLOR_SIDEBAR_TEXT);
        systemTag.setFont(FONT_SMALL);

        JLabel versionTag = new JLabel("Storage: Local CSV");
        versionTag.setForeground(COLOR_SIDEBAR_TEXT);
        versionTag.setFont(FONT_SMALL);

        footerPanel.add(systemTag);
        footerPanel.add(versionTag);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void switchView(String cardName) {
        cardLayout.show(mainContentPanel, cardName);
        for (NavButton btn : navButtons) {
            btn.setActive(btn.getTargetCard().equals(cardName));
        }
    }

    // ==================== Top Header Bar ====================

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(900, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(10, 28, 10, 28)
        ));

        // Left Header: Date display
        LocalDate today = LocalDate.now();
        String dateText = String.format("Today is %s, %s %d, %d",
                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                today.getDayOfMonth(),
                today.getYear());

        headerDateLabel = new JLabel(dateText);
        headerDateLabel.setFont(FONT_BODY);
        headerDateLabel.setForeground(COLOR_TEXT_MUTED);
        header.add(headerDateLabel, BorderLayout.WEST);

        // Right Header: Quick Balance Pill
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel balanceTitle = new JLabel("Current Balance:");
        balanceTitle.setFont(FONT_BODY_BOLD);
        balanceTitle.setForeground(COLOR_TEXT_MUTED);

        headerBalanceLabel = new JLabel("$0.00", SwingConstants.CENTER);
        headerBalanceLabel.setFont(FONT_BODY_BOLD);
        headerBalanceLabel.setOpaque(true);
        headerBalanceLabel.setBackground(COLOR_BALANCE_BG);
        headerBalanceLabel.setForeground(COLOR_BALANCE);
        headerBalanceLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BALANCE, 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        rightPanel.add(balanceTitle);
        rightPanel.add(headerBalanceLabel);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ==================== View 1: Dashboard Overview Panel ====================

    private JPanel createDashboardOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_CONTENT_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // 1. Stat Cards Row (4 cards: Income, Expense, Balance, Total Transactions)
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setOpaque(false);

        cardTotalIncome = new StatCard("Total Income", "$0.00", "All-time earnings", COLOR_INCOME, COLOR_INCOME_BG);
        cardTotalExpenses = new StatCard("Total Expenses", "$0.00", "All-time spendings", COLOR_EXPENSE, COLOR_EXPENSE_BG);
        cardCurrentBalance = new StatCard("Current Balance", "$0.00", "Net remaining balance", COLOR_BALANCE, COLOR_BALANCE_BG);
        cardTotalCount = new StatCard("Total Transactions", "0", "Recorded operations", COLOR_COUNT, COLOR_COUNT_BG);

        cardsRow.add(cardTotalIncome);
        cardsRow.add(cardTotalExpenses);
        cardsRow.add(cardCurrentBalance);
        cardsRow.add(cardTotalCount);

        // 2. Middle Banner: Financial Health & Quick Actions
        JPanel middleBanner = new JPanel(new BorderLayout(15, 0));
        middleBanner.setBackground(COLOR_CARD_BG);
        middleBanner.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JPanel healthPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        healthPanel.setOpaque(false);
        JLabel healthHeading = new JLabel("Financial Health Status");
        healthHeading.setFont(FONT_BODY_BOLD);
        healthHeading.setForeground(COLOR_TEXT_MAIN);

        savingsHealthLabel = new JLabel("Calculating balance status...");
        savingsHealthLabel.setFont(FONT_BODY);
        savingsHealthLabel.setForeground(COLOR_TEXT_MUTED);

        healthPanel.add(healthHeading);
        healthPanel.add(savingsHealthLabel);
        middleBanner.add(healthPanel, BorderLayout.WEST);

        // Quick Action Buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton btnQuickIncome = createStyledButton("+ Record Income", COLOR_INCOME, Color.WHITE);
        btnQuickIncome.addActionListener(e -> switchView("ADD_INCOME"));

        JButton btnQuickExpense = createStyledButton("- Record Expense", COLOR_EXPENSE, Color.WHITE);
        btnQuickExpense.addActionListener(e -> switchView("ADD_EXPENSE"));

        JButton btnViewAll = createStyledButton("View All History", COLOR_NAVY_BTN, Color.WHITE);
        btnViewAll.addActionListener(e -> switchView("HISTORY"));

        actionsPanel.add(btnQuickIncome);
        actionsPanel.add(btnQuickExpense);
        actionsPanel.add(btnViewAll);
        middleBanner.add(actionsPanel, BorderLayout.EAST);

        // Put Cards and Middle Banner in top section
        JPanel topContainer = new JPanel(new BorderLayout(0, 16));
        topContainer.setOpaque(false);
        topContainer.add(cardsRow, BorderLayout.NORTH);
        topContainer.add(middleBanner, BorderLayout.CENTER);
        panel.add(topContainer, BorderLayout.NORTH);

        // 3. Bottom Section: Recent Activity Table
        JPanel recentCard = new JPanel(new BorderLayout(0, 12));
        recentCard.setBackground(COLOR_CARD_BG);
        recentCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel recentTitle = new JLabel("Recent Activity");
        recentTitle.setFont(FONT_HEADER);
        recentTitle.setForeground(COLOR_TEXT_MAIN);
        recentCard.add(recentTitle, BorderLayout.NORTH);

        String[] recentColumns = {"ID", "Type", "Category", "Description", "Amount", "Date"};
        recentTableModel = new DefaultTableModel(recentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable recentTable = createStyledTable(recentTableModel);
        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        recentCard.add(scrollPane, BorderLayout.CENTER);

        panel.add(recentCard, BorderLayout.CENTER);

        return panel;
    }

    // ==================== View 2: Add Income Form Panel ====================

    private JPanel createAddIncomePanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(COLOR_CONTENT_BG);

        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(COLOR_CARD_BG);
        card.setPreferredSize(new Dimension(540, 500));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(28, 32, 28, 32)
        ));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Record New Income");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_INCOME);

        JLabel subtitle = new JLabel("Add earnings from salary, business, investments, or gifts.");
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(title);
        headerPanel.add(subtitle);
        card.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridLayout(4, 1, 0, 12));
        form.setOpaque(false);

        // Amount Field
        JPanel pAmount = new JPanel(new BorderLayout(0, 6));
        pAmount.setOpaque(false);
        JLabel lblAmount = new JLabel("Amount ($) *");
        lblAmount.setFont(FONT_BODY_BOLD);
        incomeAmountField = createModernTextField("e.g. 2500.00");
        pAmount.add(lblAmount, BorderLayout.NORTH);
        pAmount.add(incomeAmountField, BorderLayout.CENTER);

        // Category Field
        JPanel pCategory = new JPanel(new BorderLayout(0, 6));
        pCategory.setOpaque(false);
        JLabel lblCat = new JLabel("Income Category *");
        lblCat.setFont(FONT_BODY_BOLD);
        incomeCategoryBox = new JComboBox<>(Income.DEFAULT_CATEGORIES);
        incomeCategoryBox.setFont(FONT_BODY);
        incomeCategoryBox.setBackground(Color.WHITE);
        incomeCategoryBox.setPreferredSize(new Dimension(400, 38));
        pCategory.add(lblCat, BorderLayout.NORTH);
        pCategory.add(incomeCategoryBox, BorderLayout.CENTER);

        // Description Field
        JPanel pDesc = new JPanel(new BorderLayout(0, 6));
        pDesc.setOpaque(false);
        JLabel lblDesc = new JLabel("Description / Source *");
        lblDesc.setFont(FONT_BODY_BOLD);
        incomeDescField = createModernTextField("e.g. November Monthly Salary");
        pDesc.add(lblDesc, BorderLayout.NORTH);
        pDesc.add(incomeDescField, BorderLayout.CENTER);

        // Date Field
        JPanel pDate = new JPanel(new BorderLayout(0, 6));
        pDate.setOpaque(false);
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD) *");
        lblDate.setFont(FONT_BODY_BOLD);

        JPanel dateInputRow = new JPanel(new BorderLayout(8, 0));
        dateInputRow.setOpaque(false);
        incomeDateField = createModernTextField(LocalDate.now().format(Transaction.DATE_FORMATTER));
        JButton btnToday = createStyledButton("Today", COLOR_NAVY_BTN, Color.WHITE);
        btnToday.setPreferredSize(new Dimension(80, 38));
        btnToday.addActionListener(e -> incomeDateField.setText(LocalDate.now().format(Transaction.DATE_FORMATTER)));

        dateInputRow.add(incomeDateField, BorderLayout.CENTER);
        dateInputRow.add(btnToday, BorderLayout.EAST);

        pDate.add(lblDate, BorderLayout.NORTH);
        pDate.add(dateInputRow, BorderLayout.CENTER);

        form.add(pAmount);
        form.add(pCategory);
        form.add(pDesc);
        form.add(pDate);
        card.add(form, BorderLayout.CENTER);

        // Bottom Actions & Feedback
        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);

        incomeMsgLabel = new JLabel(" ", SwingConstants.CENTER);
        incomeMsgLabel.setFont(FONT_BODY_BOLD);
        bottom.add(incomeMsgLabel, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);

        JButton btnSave = createStyledButton("Save Income", COLOR_INCOME, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(180, 42));
        btnSave.addActionListener(e -> handleSaveIncome());

        JButton btnClear = createStyledButton("Clear", COLOR_CLEAR_BG, COLOR_TEXT_MAIN, COLOR_CLEAR_BORDER);
        btnClear.setPreferredSize(new Dimension(180, 42));
        btnClear.addActionListener(e -> clearIncomeForm());

        btnRow.add(btnSave);
        btnRow.add(btnClear);
        bottom.add(btnRow, BorderLayout.SOUTH);

        card.add(bottom, BorderLayout.SOUTH);
        outer.add(card);
        return outer;
    }

    private void handleSaveIncome() {
        incomeMsgLabel.setText(" ");
        String amtStr = incomeAmountField.getText().trim();
        String cat = (String) incomeCategoryBox.getSelectedItem();
        String desc = incomeDescField.getText().trim();
        String dateStr = incomeDateField.getText().trim();

        // 1. Validation
        if (amtStr.isEmpty()) {
            showFormError(incomeMsgLabel, "Please enter an amount.");
            incomeAmountField.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0.0) {
                showFormError(incomeMsgLabel, "Amount must be greater than zero.");
                incomeAmountField.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            showFormError(incomeMsgLabel, "Invalid amount. Please enter a valid number (e.g. 500.00).");
            incomeAmountField.requestFocus();
            return;
        }

        if (desc.isEmpty()) {
            showFormError(incomeMsgLabel, "Please enter a description or source.");
            incomeDescField.requestFocus();
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, Transaction.DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            showFormError(incomeMsgLabel, "Invalid date format. Use YYYY-MM-DD (e.g. 2026-11-04).");
            incomeDateField.requestFocus();
            return;
        }

        // 2. Generate unique ID & add transaction
        String newId = financeManager.generateNextId();
        Income newIncome = new Income(newId, amount, cat, desc, date);
        boolean success = financeManager.addTransaction(newIncome);

        if (success) {
            showFormSuccess(incomeMsgLabel, String.format("Success! Income %s ($%.2f) recorded and saved.", newId, amount));
            incomeAmountField.setText("");
            incomeDescField.setText("");
            incomeDateField.setText(LocalDate.now().format(Transaction.DATE_FORMATTER));
            refreshAllViews();
        } else {
            showFormError(incomeMsgLabel, "Error saving transaction to file.");
        }
    }

    private void clearIncomeForm() {
        incomeAmountField.setText("");
        incomeDescField.setText("");
        incomeDateField.setText(LocalDate.now().format(Transaction.DATE_FORMATTER));
        incomeMsgLabel.setText(" ");
    }

    // ==================== View 3: Add Expense Form Panel ====================

    private JPanel createAddExpensePanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(COLOR_CONTENT_BG);

        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(COLOR_CARD_BG);
        card.setPreferredSize(new Dimension(540, 500));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(28, 32, 28, 32)
        ));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Record New Expense");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_EXPENSE);

        JLabel subtitle = new JLabel("Track spending on food, rent, bills, shopping, travel, etc.");
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(title);
        headerPanel.add(subtitle);
        card.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridLayout(4, 1, 0, 12));
        form.setOpaque(false);

        // Amount Field
        JPanel pAmount = new JPanel(new BorderLayout(0, 6));
        pAmount.setOpaque(false);
        JLabel lblAmount = new JLabel("Amount ($) *");
        lblAmount.setFont(FONT_BODY_BOLD);
        expenseAmountField = createModernTextField("e.g. 75.50");
        pAmount.add(lblAmount, BorderLayout.NORTH);
        pAmount.add(expenseAmountField, BorderLayout.CENTER);

        // Category Field
        JPanel pCategory = new JPanel(new BorderLayout(0, 6));
        pCategory.setOpaque(false);
        JLabel lblCat = new JLabel("Expense Category *");
        lblCat.setFont(FONT_BODY_BOLD);
        expenseCategoryBox = new JComboBox<>(Expense.DEFAULT_CATEGORIES);
        expenseCategoryBox.setFont(FONT_BODY);
        expenseCategoryBox.setBackground(Color.WHITE);
        expenseCategoryBox.setPreferredSize(new Dimension(400, 38));
        pCategory.add(lblCat, BorderLayout.NORTH);
        pCategory.add(expenseCategoryBox, BorderLayout.CENTER);

        // Description Field
        JPanel pDesc = new JPanel(new BorderLayout(0, 6));
        pDesc.setOpaque(false);
        JLabel lblDesc = new JLabel("Description / Purpose *");
        lblDesc.setFont(FONT_BODY_BOLD);
        expenseDescField = createModernTextField("e.g. Weekly Grocery Shopping");
        pDesc.add(lblDesc, BorderLayout.NORTH);
        pDesc.add(expenseDescField, BorderLayout.CENTER);

        // Date Field
        JPanel pDate = new JPanel(new BorderLayout(0, 6));
        pDate.setOpaque(false);
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD) *");
        lblDate.setFont(FONT_BODY_BOLD);

        JPanel dateInputRow = new JPanel(new BorderLayout(8, 0));
        dateInputRow.setOpaque(false);
        expenseDateField = createModernTextField(LocalDate.now().format(Transaction.DATE_FORMATTER));
        JButton btnToday = createStyledButton("Today", COLOR_NAVY_BTN, Color.WHITE);
        btnToday.setPreferredSize(new Dimension(80, 38));
        btnToday.addActionListener(e -> expenseDateField.setText(LocalDate.now().format(Transaction.DATE_FORMATTER)));

        dateInputRow.add(expenseDateField, BorderLayout.CENTER);
        dateInputRow.add(btnToday, BorderLayout.EAST);

        pDate.add(lblDate, BorderLayout.NORTH);
        pDate.add(dateInputRow, BorderLayout.CENTER);

        form.add(pAmount);
        form.add(pCategory);
        form.add(pDesc);
        form.add(pDate);
        card.add(form, BorderLayout.CENTER);

        // Bottom Actions & Feedback
        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);

        expenseMsgLabel = new JLabel(" ", SwingConstants.CENTER);
        expenseMsgLabel.setFont(FONT_BODY_BOLD);
        bottom.add(expenseMsgLabel, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);

        JButton btnSave = createStyledButton("Save Expense", COLOR_EXPENSE, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(180, 42));
        btnSave.addActionListener(e -> handleSaveExpense());

        JButton btnClear = createStyledButton("Clear", COLOR_CLEAR_BG, COLOR_TEXT_MAIN, COLOR_CLEAR_BORDER);
        btnClear.setPreferredSize(new Dimension(180, 42));
        btnClear.addActionListener(e -> clearExpenseForm());

        btnRow.add(btnSave);
        btnRow.add(btnClear);
        bottom.add(btnRow, BorderLayout.SOUTH);

        card.add(bottom, BorderLayout.SOUTH);
        outer.add(card);
        return outer;
    }

    private void handleSaveExpense() {
        expenseMsgLabel.setText(" ");
        String amtStr = expenseAmountField.getText().trim();
        String cat = (String) expenseCategoryBox.getSelectedItem();
        String desc = expenseDescField.getText().trim();
        String dateStr = expenseDateField.getText().trim();

        // 1. Validation
        if (amtStr.isEmpty()) {
            showFormError(expenseMsgLabel, "Please enter an amount.");
            expenseAmountField.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0.0) {
                showFormError(expenseMsgLabel, "Amount must be greater than zero.");
                expenseAmountField.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            showFormError(expenseMsgLabel, "Invalid amount. Please enter a valid number (e.g. 75.50).");
            expenseAmountField.requestFocus();
            return;
        }

        if (desc.isEmpty()) {
            showFormError(expenseMsgLabel, "Please enter a description or purpose.");
            expenseDescField.requestFocus();
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, Transaction.DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            showFormError(expenseMsgLabel, "Invalid date format. Use YYYY-MM-DD (e.g. 2026-11-04).");
            expenseDateField.requestFocus();
            return;
        }

        // 2. Generate unique ID & add transaction
        String newId = financeManager.generateNextId();
        Expense newExpense = new Expense(newId, amount, cat, desc, date);
        boolean success = financeManager.addTransaction(newExpense);

        if (success) {
            showFormSuccess(expenseMsgLabel, String.format("Success! Expense %s ($%.2f) recorded and saved.", newId, amount));
            expenseAmountField.setText("");
            expenseDescField.setText("");
            expenseDateField.setText(LocalDate.now().format(Transaction.DATE_FORMATTER));
            refreshAllViews();
        } else {
            showFormError(expenseMsgLabel, "Error saving transaction to file.");
        }
    }

    private void clearExpenseForm() {
        expenseAmountField.setText("");
        expenseDescField.setText("");
        expenseDateField.setText(LocalDate.now().format(Transaction.DATE_FORMATTER));
        expenseMsgLabel.setText(" ");
    }

    private void showFormError(JLabel label, String text) {
        label.setForeground(COLOR_EXPENSE);
        label.setText("[!] " + text);
    }

    private void showFormSuccess(JLabel label, String text) {
        label.setForeground(COLOR_INCOME);
        label.setText("[OK] " + text);
    }

    // ==================== View 4: Transaction History Panel ====================

    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(COLOR_CONTENT_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Top Toolbar: Search, Type Filter, Delete
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(COLOR_CARD_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        // Left of toolbar: Search bar & Type filter
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftControls.setOpaque(false);

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(FONT_BODY_BOLD);
        searchIcon.setForeground(COLOR_TEXT_MAIN);

        searchField = createModernTextField("");
        searchField.setPreferredSize(new Dimension(240, 38));
        searchField.setToolTipText("Search by ID, category, description, or date");

        JButton btnSearch = createStyledButton("Search", COLOR_SIDEBAR_ACTIVE, Color.WHITE);
        btnSearch.addActionListener(e -> filterTransactionHistory());

        JButton btnClearSearch = createStyledButton("Clear", COLOR_CLEAR_BG, COLOR_TEXT_MAIN, COLOR_CLEAR_BORDER);
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            filterTypeBox.setSelectedIndex(0);
            filterTransactionHistory();
        });

        // Trigger search on Enter key
        searchField.addActionListener(e -> filterTransactionHistory());

        JLabel filterLabel = new JLabel("Type:");
        filterLabel.setFont(FONT_BODY_BOLD);
        filterLabel.setForeground(COLOR_TEXT_MAIN);

        String[] types = {"All Transactions", "Income", "Expense"};
        filterTypeBox = new JComboBox<>(types);
        filterTypeBox.setFont(FONT_BODY);
        filterTypeBox.setBackground(Color.WHITE);
        filterTypeBox.setPreferredSize(new Dimension(140, 38));
        filterTypeBox.addActionListener(e -> filterTransactionHistory());

        leftControls.add(searchIcon);
        leftControls.add(searchField);
        leftControls.add(btnSearch);
        leftControls.add(btnClearSearch);
        leftControls.add(Box.createHorizontalStrut(10));
        leftControls.add(filterLabel);
        leftControls.add(filterTypeBox);

        toolbar.add(leftControls, BorderLayout.WEST);

        // Right of toolbar: Delete & Refresh buttons
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        JButton btnDelete = createStyledButton("Delete Selected", COLOR_EXPENSE, Color.WHITE);
        btnDelete.addActionListener(e -> handleDeleteSelected());

        JButton btnRefresh = createStyledButton("Refresh", COLOR_NAVY_BTN, Color.WHITE);
        btnRefresh.addActionListener(e -> filterTransactionHistory());

        rightControls.add(btnDelete);
        rightControls.add(btnRefresh);

        toolbar.add(rightControls, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Center Table Container
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(COLOR_CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        String[] columns = {"Transaction ID", "Type", "Category", "Description", "Amount", "Date"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = createStyledTable(historyTableModel);
        historyTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Table Bottom Status Bar
        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setOpaque(false);
        historyCountLabel = new JLabel("Showing 0 transactions");
        historyCountLabel.setFont(FONT_BODY);
        historyCountLabel.setForeground(COLOR_TEXT_MUTED);

        JLabel hintLabel = new JLabel("Tip: Select multiple rows using Shift or Ctrl to delete in batch.");
        hintLabel.setFont(FONT_SMALL);
        hintLabel.setForeground(COLOR_TEXT_MUTED);

        statusRow.add(historyCountLabel, BorderLayout.WEST);
        statusRow.add(hintLabel, BorderLayout.EAST);
        tableCard.add(statusRow, BorderLayout.SOUTH);

        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private void filterTransactionHistory() {
        String query = searchField.getText().trim();
        String typeFilter = (String) filterTypeBox.getSelectedItem();

        ArrayList<Transaction> filtered = financeManager.searchTransactions(query, typeFilter);
        historyTableModel.setRowCount(0);

        for (Transaction t : filtered) {
            historyTableModel.addRow(new Object[]{
                    t.getId(),
                    t.getType(),
                    t.getCategory(),
                    t.getDescription(),
                    t.getAmount(),
                    t.getFormattedDate()
            });
        }

        historyCountLabel.setText(String.format("Showing %d of %d total transactions",
                filtered.size(), financeManager.getTransactionCount()));
    }

    private void handleDeleteSelected() {
        int[] selectedRows = historyTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select one or more transactions from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Are you sure you want to permanently delete %d selected transaction(s)?\nThis will update your balances and file storage.",
                        selectedRows.length),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            List<String> idsToDelete = new ArrayList<>();
            for (int row : selectedRows) {
                String id = (String) historyTableModel.getValueAt(row, 0);
                idsToDelete.add(id);
            }

            int deletedCount = financeManager.deleteTransactions(idsToDelete);
            JOptionPane.showMessageDialog(this,
                    String.format("Successfully deleted %d transaction(s).", deletedCount),
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);

            refreshAllViews();
        }
    }

    // ==================== View 5: Monthly Summary Panel ====================

    private JPanel createMonthlySummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(COLOR_CONTENT_BG);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header and Month/Year Selector
        JPanel filterBar = new JPanel(new BorderLayout(12, 0));
        filterBar.setBackground(COLOR_CARD_BG);
        filterBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        monthlySummaryTitle = new JLabel("Monthly Financial Summary");
        monthlySummaryTitle.setFont(FONT_HEADER);
        monthlySummaryTitle.setForeground(COLOR_TEXT_MAIN);
        filterBar.add(monthlySummaryTitle, BorderLayout.WEST);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        selectorPanel.setOpaque(false);

        JLabel lblMonth = new JLabel("Month:");
        lblMonth.setFont(FONT_BODY_BOLD);
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        summaryMonthBox = new JComboBox<>(monthNames);
        summaryMonthBox.setFont(FONT_BODY);
        summaryMonthBox.setBackground(Color.WHITE);
        summaryMonthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        JLabel lblYear = new JLabel("Year:");
        lblYear.setFont(FONT_BODY_BOLD);
        int currentYear = LocalDate.now().getYear();
        Integer[] years = {currentYear - 2, currentYear - 1, currentYear, currentYear + 1, currentYear + 2};
        summaryYearBox = new JComboBox<>(years);
        summaryYearBox.setFont(FONT_BODY);
        summaryYearBox.setBackground(Color.WHITE);
        summaryYearBox.setSelectedItem(currentYear);

        JButton btnViewSummary = createStyledButton("Filter Month", COLOR_SIDEBAR_ACTIVE, Color.WHITE);
        btnViewSummary.addActionListener(e -> updateMonthlySummary());

        JButton btnCurrentMonth = createStyledButton("Current Month", COLOR_NAVY_BTN, Color.WHITE);
        btnCurrentMonth.addActionListener(e -> {
            summaryMonthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
            summaryYearBox.setSelectedItem(LocalDate.now().getYear());
            updateMonthlySummary();
        });

        selectorPanel.add(lblMonth);
        selectorPanel.add(summaryMonthBox);
        selectorPanel.add(lblYear);
        selectorPanel.add(summaryYearBox);
        selectorPanel.add(btnViewSummary);
        selectorPanel.add(btnCurrentMonth);

        filterBar.add(selectorPanel, BorderLayout.EAST);
        panel.add(filterBar, BorderLayout.NORTH);

        // Center Content: Monthly Stats & Breakdown
        JPanel contentGrid = new JPanel(new BorderLayout(0, 16));
        contentGrid.setOpaque(false);

        // 3 Monthly Stat Cards
        JPanel monthlyCards = new JPanel(new GridLayout(1, 3, 16, 0));
        monthlyCards.setOpaque(false);

        cardMonthlyIncome = new StatCard("Monthly Income", "$0.00", "Earnings for selected month", COLOR_INCOME, COLOR_INCOME_BG);
        cardMonthlyExpenses = new StatCard("Monthly Expenses", "$0.00", "Spendings for selected month", COLOR_EXPENSE, COLOR_EXPENSE_BG);
        cardMonthlyBalance = new StatCard("Monthly Remaining Balance", "$0.00", "Income minus expenses", COLOR_BALANCE, COLOR_BALANCE_BG);

        monthlyCards.add(cardMonthlyIncome);
        monthlyCards.add(cardMonthlyExpenses);
        monthlyCards.add(cardMonthlyBalance);

        contentGrid.add(monthlyCards, BorderLayout.NORTH);

        // Split tables: Left = Category Breakdown, Right = Monthly Transactions
        JPanel tablesRow = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesRow.setOpaque(false);

        // Left Table: Category Breakdown
        JPanel catCard = new JPanel(new BorderLayout(0, 10));
        catCard.setBackground(COLOR_CARD_BG);
        catCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel catHeading = new JLabel("Category Breakdown");
        catHeading.setFont(FONT_HEADER);
        catHeading.setForeground(COLOR_TEXT_MAIN);
        catCard.add(catHeading, BorderLayout.NORTH);

        String[] catCols = {"Category", "Type", "Total Amount", "% of Month"};
        monthlyCategoryTableModel = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable catTable = createStyledTable(monthlyCategoryTableModel);
        JScrollPane catScroll = new JScrollPane(catTable);
        catScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        catScroll.getViewport().setBackground(Color.WHITE);
        catCard.add(catScroll, BorderLayout.CENTER);

        // Right Table: Monthly Transactions
        JPanel txnCard = new JPanel(new BorderLayout(0, 10));
        txnCard.setBackground(COLOR_CARD_BG);
        txnCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel txnHeading = new JLabel("Transactions in Selected Month");
        txnHeading.setFont(FONT_HEADER);
        txnHeading.setForeground(COLOR_TEXT_MAIN);
        txnCard.add(txnHeading, BorderLayout.NORTH);

        String[] txnCols = {"Date", "Type", "Category", "Description", "Amount"};
        monthlyTxnTableModel = new DefaultTableModel(txnCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable txnTable = createStyledTable(monthlyTxnTableModel);
        JScrollPane txnScroll = new JScrollPane(txnTable);
        txnScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        txnScroll.getViewport().setBackground(Color.WHITE);
        txnCard.add(txnScroll, BorderLayout.CENTER);

        tablesRow.add(catCard);
        tablesRow.add(txnCard);

        contentGrid.add(tablesRow, BorderLayout.CENTER);
        panel.add(contentGrid, BorderLayout.CENTER);

        return panel;
    }

    private void updateMonthlySummary() {
        int month = summaryMonthBox.getSelectedIndex() + 1;
        Integer year = (Integer) summaryYearBox.getSelectedItem();
        if (year == null) year = LocalDate.now().getYear();

        String monthName = (String) summaryMonthBox.getSelectedItem();
        monthlySummaryTitle.setText(String.format("Monthly Financial Summary – %s %d", monthName, year));

        double mIncome = financeManager.getMonthlyIncome(year, month);
        double mExpense = financeManager.getMonthlyExpenses(year, month);
        double mBalance = financeManager.getMonthlyBalance(year, month);

        cardMonthlyIncome.setValue(String.format("$%,.2f", mIncome));
        cardMonthlyExpenses.setValue(String.format("$%,.2f", mExpense));
        cardMonthlyBalance.setValue(String.format("$%,.2f", mBalance));

        if (mBalance >= 0) {
            cardMonthlyBalance.setValueColor(COLOR_INCOME);
            cardMonthlyBalance.setSubtitle(String.format("Net Surplus for %s", monthName));
        } else {
            cardMonthlyBalance.setValueColor(COLOR_EXPENSE);
            cardMonthlyBalance.setSubtitle(String.format("Net Deficit for %s", monthName));
        }

        // Fill Monthly Transactions Table
        ArrayList<Transaction> monthlyTxns = financeManager.getTransactionsForMonth(year, month);
        monthlyTxnTableModel.setRowCount(0);
        for (Transaction t : monthlyTxns) {
            monthlyTxnTableModel.addRow(new Object[]{
                    t.getFormattedDate(),
                    t.getType(),
                    t.getCategory(),
                    t.getDescription(),
                    t.getAmount()
            });
        }

        // Fill Category Breakdown Table
        monthlyCategoryTableModel.setRowCount(0);
        double totalVolume = mIncome + mExpense;

        Map<String, Double> incomeBreakdown = financeManager.getCategoryBreakdown(year, month, "Income");
        for (Map.Entry<String, Double> entry : incomeBreakdown.entrySet()) {
            double percent = (mIncome > 0) ? (entry.getValue() / mIncome) * 100.0 : 0.0;
            monthlyCategoryTableModel.addRow(new Object[]{
                    entry.getKey(),
                    "Income",
                    String.format("$%,.2f", entry.getValue()),
                    String.format("%.1f%% of income", percent)
            });
        }

        Map<String, Double> expenseBreakdown = financeManager.getCategoryBreakdown(year, month, "Expense");
        for (Map.Entry<String, Double> entry : expenseBreakdown.entrySet()) {
            double percent = (mExpense > 0) ? (entry.getValue() / mExpense) * 100.0 : 0.0;
            monthlyCategoryTableModel.addRow(new Object[]{
                    entry.getKey(),
                    "Expense",
                    String.format("$%,.2f", entry.getValue()),
                    String.format("%.1f%% of expense", percent)
            });
        }
    }

    // ==================== Global Refresh ====================

    /**
     * Refreshes all dashboard metrics, tables, and views with latest data.
     */
    public void refreshAllViews() {
        double totalIncome = financeManager.getTotalIncome();
        double totalExpenses = financeManager.getTotalExpenses();
        double balance = financeManager.getBalance();
        int count = financeManager.getTransactionCount();

        // 1. Top Header Balance
        headerBalanceLabel.setText(String.format("$%,.2f", balance));
        if (balance >= 0) {
            headerBalanceLabel.setForeground(COLOR_INCOME);
            headerBalanceLabel.setBackground(COLOR_INCOME_BG);
            headerBalanceLabel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_INCOME, 1, true),
                    new EmptyBorder(4, 12, 4, 12)
            ));
        } else {
            headerBalanceLabel.setForeground(COLOR_EXPENSE);
            headerBalanceLabel.setBackground(COLOR_EXPENSE_BG);
            headerBalanceLabel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_EXPENSE, 1, true),
                    new EmptyBorder(4, 12, 4, 12)
            ));
        }

        // 2. Dashboard Stat Cards
        cardTotalIncome.setValue(String.format("$%,.2f", totalIncome));
        cardTotalExpenses.setValue(String.format("$%,.2f", totalExpenses));
        cardCurrentBalance.setValue(String.format("$%,.2f", balance));
        cardTotalCount.setValue(String.valueOf(count));

        if (balance >= 0) {
            cardCurrentBalance.setValueColor(COLOR_BALANCE);
            cardCurrentBalance.setSubtitle("Healthy surplus balance");
        } else {
            cardCurrentBalance.setValueColor(COLOR_EXPENSE);
            cardCurrentBalance.setSubtitle("Expenses exceed income!");
        }

        // 3. Health status banner
        if (count == 0) {
            savingsHealthLabel.setText("No transactions recorded yet. Start by recording your income or expense.");
        } else if (totalIncome <= 0) {
            savingsHealthLabel.setText(String.format("Total Spending: $%,.2f. No income recorded yet.", totalExpenses));
        } else {
            double savingsRate = (balance / totalIncome) * 100.0;
            if (savingsRate >= 20.0) {
                savingsHealthLabel.setText(String.format("Great Job! Savings Rate is %.1f%% ($%,.2f saved from $%,.2f income).",
                        savingsRate, balance, totalIncome));
            } else if (savingsRate >= 0.0) {
                savingsHealthLabel.setText(String.format("Savings Rate is %.1f%%. Net positive balance of $%,.2f.",
                        savingsRate, balance));
            } else {
                savingsHealthLabel.setText(String.format("Deficit Alert: Spending exceeds income by $%,.2f. Consider cutting expenses.",
                        Math.abs(balance)));
            }
        }

        // 4. Recent Transactions Table
        recentTableModel.setRowCount(0);
        ArrayList<Transaction> recents = financeManager.getRecentTransactions(6);
        for (Transaction t : recents) {
            recentTableModel.addRow(new Object[]{
                    t.getId(),
                    t.getType(),
                    t.getCategory(),
                    t.getDescription(),
                    t.getAmount(),
                    t.getFormattedDate()
            });
        }

        // 5. Transaction History Table
        filterTransactionHistory();

        // 6. Monthly Summary
        updateMonthlySummary();
    }

    // ==================== UI Helper Components ====================

    private JTextField createModernTextField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(FONT_BODY);
        tf.setForeground(COLOR_TEXT_MAIN);
        tf.setPreferredSize(new Dimension(tf.getPreferredSize().width, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        return createStyledButton(text, bg, fg, null);
    }

    private JButton createStyledButton(String text, Color bg, Color fg, Color borderColor) {
        JButton btn = new JButton(text);

        // Explicitly set BasicButtonUI so Windows LookAndFeel (WindowsButtonUI)
        // does not paint native white buttons and overwrite custom colors.
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color border = (borderColor != null) ? borderColor : (isLightColor(bg) ? COLOR_CLEAR_BORDER : bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));

        Color hoverBg = getHoverColor(bg);
        Color pressedBg = getPressedColor(bg);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                    if (borderColor != null && borderColor.equals(COLOR_CLEAR_BORDER)) {
                        btn.setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(new Color(148, 163, 184), 1, true),
                                new EmptyBorder(8, 16, 8, 16)
                        ));
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(border, 1, true),
                            new EmptyBorder(8, 16, 8, 16)
                    ));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(pressedBg);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(btn.contains(e.getPoint()) ? hoverBg : bg);
                }
            }
        });

        return btn;
    }

    private static Color getHoverColor(Color bg) {
        if (bg.equals(COLOR_INCOME)) return COLOR_INCOME_HOVER;
        if (bg.equals(COLOR_EXPENSE)) return COLOR_EXPENSE_HOVER;
        if (bg.equals(COLOR_NAVY_BTN)) return COLOR_NAVY_HOVER;
        if (bg.equals(COLOR_PRIMARY_BLUE)) return COLOR_PRIMARY_HOVER;
        if (bg.equals(COLOR_CLEAR_BG)) return COLOR_CLEAR_HOVER;

        if (isLightColor(bg)) {
            return new Color(
                    Math.max(0, bg.getRed() - 15),
                    Math.max(0, bg.getGreen() - 15),
                    Math.max(0, bg.getBlue() - 15)
            );
        } else {
            return new Color(
                    Math.min(255, (int) (bg.getRed() * 1.15)),
                    Math.min(255, (int) (bg.getGreen() * 1.15)),
                    Math.min(255, (int) (bg.getBlue() * 1.15))
            );
        }
    }

    private static Color getPressedColor(Color bg) {
        return new Color(
                Math.max(0, (int) (bg.getRed() * 0.75)),
                Math.max(0, (int) (bg.getGreen() * 0.75)),
                Math.max(0, (int) (bg.getBlue() * 0.75))
        );
    }

    private static boolean isLightColor(Color c) {
        double luminance = (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue()) / 255.0;
        return luminance > 0.65;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(COLOR_TEXT_MAIN);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Custom Cell Renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                setBorder(new EmptyBorder(0, 10, 0, 10));

                String colName = tbl.getColumnName(col).toLowerCase();
                String text = (value != null) ? value.toString() : "";

                if (colName.contains("type")) {
                    setFont(FONT_BODY_BOLD);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if ("Income".equalsIgnoreCase(text)) {
                        setForeground(COLOR_INCOME);
                    } else if ("Expense".equalsIgnoreCase(text)) {
                        setForeground(COLOR_EXPENSE);
                    }
                } else if (colName.contains("amount")) {
                    setFont(FONT_BODY_BOLD);
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    try {
                        double amt = Double.parseDouble(text.replace("$", "").replace(",", "").trim());
                        // Check type in row if available
                        int typeCol = findColumnIndex(tbl, "type");
                        String rowType = typeCol >= 0 ? tbl.getValueAt(row, typeCol).toString() : "";
                        if ("Income".equalsIgnoreCase(rowType)) {
                            setText(String.format("+$%,.2f", amt));
                            setForeground(COLOR_INCOME);
                        } else if ("Expense".equalsIgnoreCase(rowType)) {
                            setText(String.format("-$%,.2f", amt));
                            setForeground(COLOR_EXPENSE);
                        } else {
                            setText(String.format("$%,.2f", amt));
                            setForeground(COLOR_TEXT_MAIN);
                        }
                    } catch (NumberFormatException ignored) {
                        setText(text);
                    }
                } else if (colName.contains("date") || colName.contains("id")) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(COLOR_TEXT_MUTED);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setForeground(COLOR_TEXT_MAIN);
                }

                return c;
            }
        });

        return table;
    }

    private static int findColumnIndex(JTable table, String name) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).toLowerCase().contains(name.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    // ==================== Inner Class: NavButton ====================

    private class NavButton extends JButton {
        private final String targetCard;
        private boolean isActive = false;

        public NavButton(String text, String targetCard) {
            super(text);
            this.targetCard = targetCard;

            // Explicitly set BasicButtonUI to avoid Windows LookAndFeel overriding sidebar button styling
            setUI(new javax.swing.plaf.basic.BasicButtonUI());
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setForeground(COLOR_SIDEBAR_TEXT);
            setBackground(COLOR_SIDEBAR_BG);
            setFocusPainted(false);
            setOpaque(true);
            setContentAreaFilled(true);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_SIDEBAR_BG, 1, true),
                    new EmptyBorder(10, 16, 10, 16)
            ));
            setMaximumSize(new Dimension(210, 44));
            setPreferredSize(new Dimension(210, 44));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
                        setBackground(COLOR_SIDEBAR_HOVER);
                        setForeground(Color.WHITE);
                        setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(COLOR_SIDEBAR_HOVER, 1, true),
                                new EmptyBorder(10, 16, 10, 16)
                        ));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) {
                        setBackground(COLOR_SIDEBAR_BG);
                        setForeground(COLOR_SIDEBAR_TEXT);
                        setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(COLOR_SIDEBAR_BG, 1, true),
                                new EmptyBorder(10, 16, 10, 16)
                        ));
                    }
                }
            });

            addActionListener(e -> switchView(targetCard));
        }

        public String getTargetCard() {
            return targetCard;
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setBackground(COLOR_SIDEBAR_ACTIVE);
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_SIDEBAR_ACTIVE, 1, true),
                        new EmptyBorder(10, 16, 10, 16)
                ));
            } else {
                setBackground(COLOR_SIDEBAR_BG);
                setForeground(COLOR_SIDEBAR_TEXT);
                setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(COLOR_SIDEBAR_BG, 1, true),
                        new EmptyBorder(10, 16, 10, 16)
                ));
            }
        }
    }

    // ==================== Inner Class: StatCard ====================

    public static class StatCard extends JPanel {
        private final JLabel titleLabel;
        private final JLabel valueLabel;
        private final JLabel subtitleLabel;

        public StatCard(String title, String initialValue, String subtitle, Color accentColor, Color bgTint) {
            setLayout(new BorderLayout(0, 6));
            setBackground(COLOR_CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(18, 20, 18, 20)
            ));

            // Top Header: Title and Accent Bar
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);

            titleLabel = new JLabel(title);
            titleLabel.setFont(FONT_BODY_BOLD);
            titleLabel.setForeground(COLOR_TEXT_MUTED);

            JPanel accentDot = new JPanel();
            accentDot.setPreferredSize(new Dimension(10, 10));
            accentDot.setBackground(accentColor);
            accentDot.setBorder(new LineBorder(accentColor, 1, true));

            top.add(titleLabel, BorderLayout.WEST);
            top.add(accentDot, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            // Center: Big Value
            valueLabel = new JLabel(initialValue);
            valueLabel.setFont(FONT_CARD_VALUE);
            valueLabel.setForeground(accentColor);
            add(valueLabel, BorderLayout.CENTER);

            // Bottom: Subtitle
            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(FONT_SMALL);
            subtitleLabel.setForeground(COLOR_TEXT_MUTED);
            add(subtitleLabel, BorderLayout.SOUTH);
        }

        public void setValue(String val) {
            valueLabel.setText(val);
        }

        public void setValueColor(Color c) {
            valueLabel.setForeground(c);
        }

        public void setSubtitle(String sub) {
            subtitleLabel.setText(sub);
        }
    }
}
