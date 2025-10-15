package view;

import controller.ExpenseController;
import model.Ledger;
import model.Transaction;
import model.Budget; // ADDED: To resolve Budget cannot be resolved error

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * View: The main GUI of the application (Java Swing).
 * Implements IObserver to automatically update when the Ledger (Model) changes.
 * (Requirement: MVC, Observer Pattern, User Interface Views)
 */
public class ExpenseView extends JFrame implements IObserver {
    private final Ledger model;
    private final ExpenseController controller;

    private final JTextArea transactionListArea = new JTextArea(10, 40);
    private final JLabel summaryLabel = new JLabel("Summary: N/A");
    private final JLabel budgetLabel = new JLabel("Budget Status: N/A");

    /**
     * Requires: model and controller are not null.
     * Effects: Constructs and displays the main application window.
     */
    public ExpenseView(Ledger model, ExpenseController controller) {
        super("SE-4111 Expense Tracker");
        this.model = model;
        this.controller = controller;

        // Register as an Observer (required for the pattern)
        model.addObserver(this);

        // Frame Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(600, 400)); // Good practice for responsive design

        // Main Panel with Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Transaction", createAddTransactionPanel());
        tabbedPane.addTab("View Transactions", createTransactionListPanel());
        tabbedPane.addTab("Summary & Budget", createSummaryBudgetPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // Status bar at the bottom
        JPanel statusBar = new JPanel(new GridLayout(1, 2));
        statusBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        statusBar.add(summaryLabel);
        statusBar.add(budgetLabel);
        add(statusBar, BorderLayout.SOUTH);

        // Initial update and display
        update();
        setVisible(true);

        // Add a shutdown hook to ensure data is saved when the window is closed
        Runtime.getRuntime().addShutdownHook(new Thread(controller::saveState));
    }

    /**
     * Creates the panel for adding new transactions.
     * (Functional Requirement: Add Expense/Income Transaction)
     */
    private JPanel createAddTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<Transaction.Type> typeChooser = new JComboBox<>(Transaction.Type.values());
        JTextField descriptionField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JTextField categoryField = new JTextField(12);
        JTextField dateField = new JTextField(10); // YYYY-MM-DD or today/yesterday/tomorrow
        JButton addButton = new JButton("Add Transaction");

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeChooser);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateField);
        formPanel.add(addButton);

        panel.add(formPanel, BorderLayout.CENTER);

        addButton.addActionListener((ActionEvent e) -> {
            try {
                Transaction.Type type = (Transaction.Type) typeChooser.getSelectedItem();
                String description = descriptionField.getText();
                String amountText = amountField.getText();
                String category = categoryField.getText();
                String dateStr = dateField.getText();

                double amount = Double.parseDouble(amountText);
                controller.addTransactionExplicit(description, amount, category, dateStr, type);
                JOptionPane.showMessageDialog(this, "Transaction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                descriptionField.setText("");
                amountField.setText("");
                categoryField.setText("");
                dateField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Creates the panel for viewing and managing transactions.
     * (Functional Requirement: View Transaction List, Edit and Delete Transactions)
     */
    private JPanel createTransactionListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        transactionListArea.setEditable(false);
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout());
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"ALL", "EXPENSE", "INCOME"});
        JTextField categoryFilter = new JTextField(10);
        JTextField startFilter = new JTextField(8);
        JTextField endFilter = new JTextField(8);
        JButton applyFilters = new JButton("Apply Filters");

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);
        filterPanel.add(new JLabel("Start:"));
        filterPanel.add(startFilter);
        filterPanel.add(new JLabel("End:"));
        filterPanel.add(endFilter);
        filterPanel.add(applyFilters);

        panel.add(filterPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(transactionListArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Simple Delete Button
        JButton deleteButton = new JButton("Delete Selected (Enter ID prefix, e.g., first 8 chars)");
        JTextField deleteIdField = new JTextField(10);
        JPanel deletePanel = new JPanel(new FlowLayout());
        deletePanel.add(new JLabel("ID to Delete:"));
        deletePanel.add(deleteIdField);
        deletePanel.add(deleteButton);
        panel.add(deletePanel, BorderLayout.SOUTH);

        deleteButton.addActionListener((ActionEvent e) -> {
            String id = deleteIdField.getText().trim();
            if (id.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Please enter a Transaction ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            // Custom Confirmation Modal (instead of confirm() as required)
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete transaction " + id + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.deleteTransaction(id);
                    JOptionPane.showMessageDialog(this, "Transaction deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    deleteIdField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Deletion Error: Invalid ID or internal error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- Edit Section ---
        JPanel editPanel = new JPanel(new FlowLayout());
        JTextField editIdField = new JTextField(10);
        JTextField editDescriptionField = new JTextField(10);
        JTextField editAmountField = new JTextField(8);
        JTextField editCategoryField = new JTextField(10);
        JTextField editDateField = new JTextField(10);
        JButton editButton = new JButton("Edit by ID Prefix");

        editPanel.add(new JLabel("ID Prefix:"));
        editPanel.add(editIdField);
        editPanel.add(new JLabel("Description:"));
        editPanel.add(editDescriptionField);
        editPanel.add(new JLabel("Amount:"));
        editPanel.add(editAmountField);
        editPanel.add(new JLabel("Category:"));
        editPanel.add(editCategoryField);
        editPanel.add(new JLabel("Date:"));
        editPanel.add(editDateField);
        editPanel.add(editButton);

        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(deletePanel);
        south.add(editPanel);
        panel.add(south, BorderLayout.SOUTH);

        editButton.addActionListener((ActionEvent e) -> {
            String idPrefix = editIdField.getText().trim();
            if (idPrefix.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID prefix.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String desc = editDescriptionField.getText();
            String amtText = editAmountField.getText().trim();
            String cat = editCategoryField.getText();
            String dateStr = editDateField.getText();

            Double amt = null;
            if (!amtText.isEmpty()) {
                try {
                    amt = Double.parseDouble(amtText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                controller.editTransactionByPrefix(idPrefix, desc.isEmpty() ? null : desc, amt, cat.isEmpty() ? null : cat, dateStr.isEmpty() ? null : dateStr, null);
                JOptionPane.showMessageDialog(this, "Transaction updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                editIdField.setText("");
                editDescriptionField.setText("");
                editAmountField.setText("");
                editCategoryField.setText("");
                editDateField.setText("");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        applyFilters.addActionListener((ActionEvent e) -> {
            try {
                String typeSel = (String) typeFilter.getSelectedItem();
                String cat = categoryFilter.getText().trim();
                String startStr = startFilter.getText().trim();
                String endStr = endFilter.getText().trim();

                java.time.LocalDate start = startStr.isEmpty() ? java.time.LocalDate.MIN : java.time.LocalDate.parse(startStr);
                java.time.LocalDate end = endStr.isEmpty() ? java.time.LocalDate.MAX : java.time.LocalDate.parse(endStr);

                StringBuilder sbList = new StringBuilder("--- Filtered Transactions ---\n");
                sbList.append(String.format("%-10s %-10s %-12s %-10s %s\n", "ID (Short)", "Date", "Type", "Amount", "Description"));
                sbList.append("------------------------------------------------------------------------\n");

                double totalExpense = 0, totalIncome = 0;
                for (Transaction t : model.getAllTransactionsSorted()) {
                    if (!cat.isEmpty() && !t.getCategory().equalsIgnoreCase(cat)) continue;
                    if (t.getDate().isBefore(start) || t.getDate().isAfter(end)) continue;
                    if (!"ALL".equals(typeSel) && !t.getType().toString().equals(typeSel)) continue;

                    sbList.append(String.format("%-10s %-10s %-12s %-10.2f %s\n",
                            t.getId().getID().substring(0, 8),
                            t.getDate().format(java.time.format.DateTimeFormatter.ISO_DATE),
                            t.getType(),
                            t.getAmount(),
                            t.getDescription()));

                    if (t.getType() == Transaction.Type.EXPENSE) totalExpense += t.getAmount(); else totalIncome += t.getAmount();
                }
                sbList.append(String.format("\nTotals — Income: $%.2f, Expense: $%.2f\n", totalIncome, totalExpense));
                transactionListArea.setText(sbList.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid dates (YYYY-MM-DD) or leave blank.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Creates the panel for displaying summary and budget information.
     * (Functional Requirement: Display Summary, Set Budget, Budget Warning)
     */
    private JPanel createSummaryBudgetPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea summaryArea = new JTextArea(15, 30);
        summaryArea.setEditable(false);
        panel.add(new JScrollPane(summaryArea), BorderLayout.NORTH);

        // Date Range Summary Controls
        JPanel rangePanel = new JPanel(new FlowLayout());
        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);
        JButton refreshSummaryButton = new JButton("Refresh Summary");

        rangePanel.add(new JLabel("Start (YYYY-MM-DD):"));
        rangePanel.add(startDateField);
        rangePanel.add(new JLabel("End (YYYY-MM-DD):"));
        rangePanel.add(endDateField);
        rangePanel.add(refreshSummaryButton);

        // Budget Control
        JPanel budgetPanel = new JPanel(new FlowLayout());
        JTextField budgetLimitField = new JTextField(10);
        JComboBox<Budget.Period> periodChooser = new JComboBox<>(Budget.Period.values());
        JButton setBudgetButton = new JButton("Set Budget");

        budgetPanel.add(new JLabel("Set Limit:"));
        budgetPanel.add(budgetLimitField);
        budgetPanel.add(new JLabel("Period:"));
        budgetPanel.add(periodChooser);
        budgetPanel.add(setBudgetButton);
        JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(rangePanel);
        center.add(budgetPanel);
        panel.add(center, BorderLayout.CENTER);

        setBudgetButton.addActionListener(e -> {
            try {
                double limit = Double.parseDouble(budgetLimitField.getText());
                Budget.Period period = (Budget.Period) periodChooser.getSelectedItem();
                controller.setBudget(limit, period);
                JOptionPane.showMessageDialog(this, "Budget set successfully!", "Budget Update", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the budget limit.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Store the summary area for refreshing
        this.summaryAreaRef = summaryArea;

        // Hook up refresh for date-range summaries (expense + income categories)
        refreshSummaryButton.addActionListener(e -> {
            try {
                java.time.LocalDate start = java.time.LocalDate.parse(startDateField.getText().trim());
                java.time.LocalDate end = java.time.LocalDate.parse(endDateField.getText().trim());

                StringBuilder sb = new StringBuilder();
                sb.append("--- Category Summary (Expense) ---\n");
                Map<String, Double> exp = model.getCategorySummaryByDateRange(Transaction.Type.EXPENSE, start, end);
                exp.forEach((k, v) -> sb.append(String.format("%-15s: $%.2f\n", k, v)));
                sb.append("\n--- Category Summary (Income) ---\n");
                Map<String, Double> inc = model.getCategorySummaryByDateRange(Transaction.Type.INCOME, start, end);
                inc.forEach((k, v) -> sb.append(String.format("%-15s: $%.2f\n", k, v)));
                summaryAreaRef.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid start/end dates (YYYY-MM-DD).", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }

    // Reference to the summary text area
    private JTextArea summaryAreaRef;

    /**
     * Design Pattern: Observer Method
     * Requires: True
     * Effects: Queries the Ledger (Model) for updated data and refreshes all View components.
     */
    @Override
    public void update() {
        // --- 1. Update Transaction List View ---
        StringBuilder sbList = new StringBuilder("--- All Transactions (Newest First) ---\n");
        sbList.append(String.format("%-10s %-10s %-12s %-10s %s\n", "ID (Short)", "Date", "Type", "Amount", "Description"));
        sbList.append("------------------------------------------------------------------------\n");

        List<Transaction> transactions = model.getAllTransactionsSorted();
        double totalExpense = 0;
        double totalIncome = 0;

        for (Transaction t : transactions) {
            sbList.append(String.format("%-10s %-10s %-12s %-10.2f %s\n",
                    t.getId().getID().substring(0, 8),
                    t.getDate().format(DateTimeFormatter.ISO_DATE),
                    t.getType(),
                    t.getAmount(),
                    t.getDescription()));

            if (t.getType() == Transaction.Type.EXPENSE) {
                totalExpense += t.getAmount();
            } else {
                totalIncome += t.getAmount();
            }
        }
        transactionListArea.setText(sbList.toString());


        // --- 2. Update Summary ---
        String currentMonth = LocalDate.now().getMonth().toString();
        double net = totalIncome - totalExpense;
        summaryLabel.setText(String.format("Net: $%.2f (Income: $%.2f | Expense: $%.2f)", net, totalIncome, totalExpense));

        // Update the detailed summary panel
        StringBuilder sbSummary = new StringBuilder();
        sbSummary.append("--- Monthly Expense Summary (").append(currentMonth).append(") ---\n");
        Map<String, Double> categorySummary = model.getMonthlyExpenseSummaryByCategory(LocalDate.now());

        double totalMonthlyExpense = categorySummary.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Map.Entry<String, Double> entry : categorySummary.entrySet()) {
            sbSummary.append(String.format("%-15s: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        sbSummary.append("-------------------------------------\n");
        sbSummary.append(String.format("Total Monthly Expense: $%.2f\n\n", totalMonthlyExpense));

        // --- 3. Update Budget Status ---
        Budget budget = model.getBudget();
        LocalDate startDate = budget.getPeriodStartDate(LocalDate.now());
        LocalDate endDate = budget.getPeriodEndDate(LocalDate.now());

        // Calculate total expense within the current budget period
        double periodExpense = model.calculateTotal(Transaction.Type.EXPENSE, startDate, endDate);
        double limit = budget.getLimit();

        String budgetStatus;
        if (periodExpense >= limit) {
            // Budget Warning (Required Functional Implementation)
            budgetStatus = String.format("Budget Status: OVERSPENT! (Spent: $%.2f / Limit: $%.2f)", periodExpense, limit);
            budgetLabel.setForeground(Color.RED);
        } else if (periodExpense >= limit * 0.8) {
            budgetStatus = String.format("Budget Status: APPROACHING LIMIT (Spent: $%.2f / Limit: $%.2f)", periodExpense, limit);
            budgetLabel.setForeground(Color.ORANGE);
        } else {
            budgetStatus = String.format("Budget Status: OK (Spent: $%.2f / Limit: $%.2f)", periodExpense, limit);
            budgetLabel.setForeground(Color.BLACK);
        }

        sbSummary.append("--- Budget Tracking (").append(budget.getPeriod()).append(": ").append(startDate).append(" to ").append(endDate).append(") ---\n");
        sbSummary.append(budgetStatus.replace("Budget Status: ", ""));
        sbSummary.append("\nRemaining: $").append(String.format("%.2f", Math.max(0, limit - periodExpense)));

        summaryAreaRef.setText(sbSummary.toString());
        budgetLabel.setText(budgetStatus); // update status bar clearly
    }
}
