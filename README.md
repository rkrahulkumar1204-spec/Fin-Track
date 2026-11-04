# FinTrack – Personal Finance Manager
**100% Pure Core Java Desktop Application**

FinTrack is an offline personal finance management application built strictly with **Core Java (JDK 17)** and **Java Swing**. It enables users to record income and expenses, automatically calculate remaining balances, track transaction history, and analyze monthly financial summaries—persisting all data reliably using local CSV file handling.

Designed for academic evaluation and real-world personal budgeting, FinTrack demonstrates textbook implementation of **Object-Oriented Programming (OOP)** concepts including Abstraction, Encapsulation, Inheritance, and Polymorphism without using any external libraries or databases.

---

## Table of Contents
1. [Core Features](#core-features)
2. [Technology Stack](#technology-stack)
3. [OOP Architecture & Design Patterns](#oop-architecture--design-patterns)
4. [Project Structure](#project-structure)
5. [Class Specifications](#class-specifications)
6. [Data Storage (CSV Format)](#data-storage-csv-format)
7. [How to Compile and Run](#how-to-compile-and-run)
8. [Input Validation & Error Handling](#input-validation--error-handling)

---

## Core Features

### 1. Dashboard Overview
- **Metric Cards**: Real-time summary displaying **Total Income**, **Total Expenses**, **Current Balance**, and **Total Transactions**.
- **Financial Health Status**: Dynamic savings rate analysis (calculates percentage of income saved and alerts on deficits).
- **Recent Activity Table**: View the most recent transactions with type, category, date, and signed amount.
- **Quick Action Buttons**: Instant navigation shortcuts to record income or expenses.

### 2. Add Income
- Record earnings with validated positive amounts.
- Preset categories: *Salary, Business, Freelance, Investments, Rental Income, Grants & Gifts, Refunds, Interest, Other Income*.
- Automatically defaults to current date (`YYYY-MM-DD`) with one-click **"Today"** button.
- Mandatory description/source field.

### 3. Add Expense
- Record expenditures with validated positive amounts.
- Preset categories: *Food & Dining, Groceries, Housing & Rent, Utilities, Transportation, Shopping, Entertainment, Healthcare & Medical, Education, Personal Care, Travel, Bills & Fees, Other Expense*.
- Automatically defaults to current date (`YYYY-MM-DD`).
- Mandatory description/purpose field.

### 4. Transaction History
- Interactive `JTable` displaying: **Transaction ID**, **Type**, **Category**, **Description**, **Amount**, and **Date**.
- **Live Search & Filter**: Search across IDs, descriptions, categories, or dates.
- **Type Filter**: Filter instantly by *All Transactions*, *Income Only*, or *Expense Only*.
- **Batch Deletion**: Select one or multiple transactions (using `Shift` or `Ctrl`) and delete with confirmation. Deletions immediately update current balance and sync with the CSV file.

### 5. Automatic Balance Calculation
$$\text{Current Balance} = \text{Total Income} - \text{Total Expenses}$$
- Dynamically highlights surplus (Green) or deficit (Red).
- Real-time updates across the dashboard and top navigation bar upon adding or deleting records.

### 6. Monthly Financial Summary
- Filter transactions by **Month** (January–December) and **Year**.
- Monthly overview cards: **Monthly Income**, **Monthly Expenses**, and **Monthly Remaining Balance**.
- **Category Breakdown Table**: Groups spending and earnings by category with exact dollar totals and percentage share of the monthly total.
- **Monthly Transactions Table**: Lists all transactions recorded within the selected month.

---

## Technology Stack

| Component | Technology | Description |
|---|---|---|
| **Programming Language** | Core Java (JDK 17 LTS) | 100% Standard Java SE |
| **Graphical User Interface** | Java Swing & AWT | Modern styled custom desktop GUI |
| **Data Persistence** | File Handling (`java.io`) | Local CSV file (`data/transactions.csv`) |
| **Collections Framework** | `java.util.ArrayList` | In-memory transaction storage |
| **Date & Time API** | `java.time.LocalDate` | Robust ISO-8601 date management |
| **External Dependencies** | **None (0%)** | Zero third-party libraries, Spring, or databases |

---

## OOP Architecture & Design Patterns

The project is structured according to clean Object-Oriented principles:

```
                  +-----------------------------------+
                  |      <<abstract>> Transaction     |
                  +-----------------------------------+
                  | - id: String                      |
                  | - amount: double                  |
                  | - category: String                |
                  | - description: String             |
                  | - date: LocalDate                 |
                  +-----------------------------------+
                  | + abstract getType(): String      |
                  | + abstract getSignedAmount(): dbl |
                  +-----------------------------------+
                                    ^
                                    |
                    +---------------+---------------+
                    |                               |
       +-------------------------+     +-------------------------+
       |         Income          |     |         Expense         |
       +-------------------------+     +-------------------------+
       | + getType(): "Income"   |     | + getType(): "Expense"  |
       | + getSignedAmount(): +amt|    | + getSignedAmount(): -amt|
       +-------------------------+     +-------------------------+
```

### OOP Concepts Demonstrated:

1. **Abstraction**:
   - `Transaction` is an `abstract` base class that defines the core structure of financial transactions while leaving type identification (`getType()`) and sign evaluation (`getSignedAmount()`) to specialized subclasses.
2. **Inheritance**:
   - `Income` and `Expense` extend `Transaction`, reusing base attributes (`id`, `amount`, `category`, `description`, `date`) via constructor chaining (`super(...)`).
3. **Polymorphism**:
   - Polymorphic method calls: `FinanceManager` maintains a uniform collection `ArrayList<Transaction>`. Methods like `t.getType()`, `t.getSignedAmount()`, and `t.getFormattedAmount()` execute subclass-specific logic polymorphically.
4. **Encapsulation**:
   - All fields are strictly `private`. State mutation occurs through guarded getters and setters with comprehensive validation (rejecting negative/zero amounts, null or blank descriptions/categories, and invalid dates).
5. **Separation of Concerns (MVC Pattern)**:
   - **Model**: `Transaction`, `Income`, `Expense`
   - **Controller / Service**: `FinanceManager`
   - **Persistence / Data Access**: `FileHandler`
   - **View / Presentation**: `Dashboard`, `Main`

---

## Project Structure

```
d:\FinTrack\
│
├── src\
│   └── com\
│       └── fintrack\
│           ├── Main.java             # Application launcher and Look & Feel configuration
│           ├── Transaction.java      # Abstract base class (Model)
│           ├── Income.java           # Income subclass (Model)
│           ├── Expense.java          # Expense subclass (Model)
│           ├── FinanceManager.java   # Central business logic and calculations (Controller)
│           ├── FileHandler.java      # CSV reading, writing, and parsing (Data Access)
│           └── Dashboard.java        # Modern Swing GUI and event handlers (View)
│
├── bin\                              # Compiled .class bytecode
├── data\
│   └── transactions.csv              # Local persistent CSV storage
│
├── compile.bat                       # One-click Windows compilation script
├── run.bat                           # One-click Windows launch script
├── package.bat                       # Standalone Executable JAR packaging script
├── FinTrack.jar                      # Ready-to-run executable JAR
└── README.md                         # Project documentation
```

---

## Class Specifications

### 1. `Transaction.java` (Abstract Base Class)
- **Responsibility**: Holds shared transaction state (`id`, `amount`, `category`, `description`, `date`).
- **Key Methods**:
  - `public abstract String getType();`
  - `public abstract double getSignedAmount();`
  - `public String getFormattedAmount();` (formats as `$1,250.00`)
  - `public String getFormattedDate();` (formats as `YYYY-MM-DD`)

### 2. `Income.java`
- **Responsibility**: Represents incoming funds.
- **Key Methods**:
  - Implements `getType()` returning `"Income"`.
  - Implements `getSignedAmount()` returning `+amount`.
  - Provides `DEFAULT_CATEGORIES` for income sources.

### 3. `Expense.java`
- **Responsibility**: Represents outgoing expenditures.
- **Key Methods**:
  - Implements `getType()` returning `"Expense"`.
  - Implements `getSignedAmount()` returning `-amount`.
  - Provides `DEFAULT_CATEGORIES` for expense items.

### 4. `FileHandler.java`
- **Responsibility**: Handles file persistence to `data/transactions.csv`.
- **Key Features**:
  - Full CSV quoting support: automatically wraps and escapes values containing commas or quotes.
  - Safe line parser: custom state-machine tokenizer that preserves commas inside quoted descriptions.
  - Fault tolerance: skips corrupted rows gracefully without corrupting existing records.

### 5. `FinanceManager.java`
- **Responsibility**: Central business controller storing transactions in `ArrayList<Transaction>`.
- **Key Methods**:
  - `addTransaction(Transaction t)`: Inserts transaction and triggers disk persistence.
  - `deleteTransaction(String id)` / `deleteTransactions(List<String> ids)`: Deletes and re-saves.
  - `getTotalIncome()`, `getTotalExpenses()`, `getBalance()`.
  - `getMonthlyIncome(year, month)`, `getMonthlyExpenses(year, month)`, `getMonthlyBalance(year, month)`.
  - `getCategoryBreakdown(year, month, type)`: Category-level spending/earning aggregation.
  - `generateNextId()`: Generates sequential IDs (`TXN-1001`, `TXN-1002`, ...) guaranteed not to collide.
  - `searchTransactions(query, typeFilter)`: Case-insensitive search across ID, description, category, and date.

### 6. `Dashboard.java`
- **Responsibility**: Primary Swing desktop window (`JFrame`) with modern UX.
- **Key Components**:
  - Dark Slate sidebar (`#0F172A`) with interactive navigation buttons.
  - CardLayout manager switching between Overview, Add Income, Add Expense, History, and Monthly Summary.
  - Custom table styling with zebra striping, badge renderers for transaction types, and aligned currency values.

### 7. `Main.java`
- **Responsibility**: Entry point. Sets anti-aliased font hints, configures native System Look and Feel, initializes `FinanceManager`, and displays `Dashboard` on the Swing Event Dispatch Thread (EDT).

---

## Data Storage (CSV Format)

All transaction data is stored locally in `data/transactions.csv`.

**CSV Header:**
```csv
id,type,amount,category,description,date
```

**Example Data:**
```csv
TXN-1001,Income,4500.00,Salary,November Tech Salary,2026-11-01
TXN-1002,Expense,1200.00,Housing & Rent,"Apartment Rent, Floor 4",2026-11-02
TXN-1003,Expense,85.50,Food & Dining,Weekly Grocery Run,2026-11-03
```

- When the application starts, data is automatically loaded into memory.
- Any added or deleted transaction is updated immediately to disk.

---

## How to Compile and Run

### Prerequisites
- Java Development Kit (JDK 17 or higher) installed.
- `javac` and `java` available on your system path.

### Option A: Using Batch Scripts (Windows)
1. Double-click `compile.bat` to compile the Java classes.
2. Double-click `run.bat` to launch the application.

### Option B: Using Command Line
Open PowerShell or Command Prompt in `d:\FinTrack`:

```powershell
# 1. Compile all source files into bin directory
javac -encoding UTF-8 -d bin src/com/fintrack/*.java

# 2. Run the application
java -cp bin com.fintrack.Main
```

### Option C: Using Standalone Executable JAR
```powershell
# Run the pre-built JAR file directly:
java -jar FinTrack.jar
```
*(Or double-click `FinTrack.jar` directly in Windows Explorer).*

---

## Input Validation & Error Handling

- **Non-Positive Amounts**: Rejects `0.0` or negative values with clear messages (`"Amount must be greater than zero."`).
- **Non-Numeric Inputs**: Catches `NumberFormatException` and alerts user.
- **Empty Fields**: Verifies that description, category, and amount are not blank.
- **Date Formatting**: Enforces `YYYY-MM-DD` ISO-8601 format using `java.time.LocalDate`.
- **Deletion Safety**: Prompts user with confirmation dialog displaying the exact number of selected transactions before permanently deleting.

