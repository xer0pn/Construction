package controller;

import model.Ledger;
import model.PersistenceManager;
import model.Transaction;
// import model.Budget; // REMOVED: Compiler indicated this import is unused.
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller: Manages user input, translates it into Model commands, and handles validation.
 */
public class ExpenseController {
    private final Ledger model;

    /**
     * Requires: model is not null.
     * Effects: Constructs the controller, linking it to the Ledger model.
     */
    public ExpenseController(Ledger model) {
        this.model = model;
    }

    /**
     * Requires: True
     * Effects: Saves the current state of the Ledger to persistent storage.
     */
    public void saveState() {
        PersistenceManager.getInstance().saveLedger(model);
    }

    /**
     * Requires: description, category non-empty; amount > 0; dateStr is YYYY-MM-DD or keywords.
     * Effects: Validates explicit fields and adds a new transaction.
     */
    public void addTransactionExplicit(String description, double amount, String category, String dateStr, Transaction.Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is required.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        LocalDate date = LocalDate.now();
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            String s = dateStr.trim().toLowerCase();
            try {
                switch (s) {
                    case "today":
                        date = LocalDate.now();
                        break;
                    case "yesterday":
                        date = LocalDate.now().minusDays(1);
                        break;
                    case "tomorrow":
                        date = LocalDate.now().plusDays(1);
                        break;
                    default:
                        date = LocalDate.parse(dateStr.trim());
                        break;
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD, today, yesterday, or tomorrow.");
            }
        }

        Transaction t = new Transaction(type, amount, date, category.trim(), description.trim());
        model.addTransaction(t);
    }

    /**
     * Requires: input is a non-null string.
     * Effects: Parses a complex transaction string using Regular Expressions, validates data,
     * and adds a new transaction to the model.
     * (Requirement: Regular Expressions, Validation)
     *
     * @param input The natural language input string (e.g., "coffee $5.50 category:food on:yesterday").
     * @param type The type of transaction (EXPENSE or INCOME).
     * @param defaultCategory The category to use if not specified in the input.
     * @throws IllegalArgumentException if the input is invalid or parsing fails.
     */
    public void addTransactionFromInput(String input, Transaction.Type type, String defaultCategory) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        // Regex with explicit named-like groups (Java doesn't support true named groups pre-9, so we document indices):
        // 1: description (text before amount)
        // 2: amount (required numeric, optional $ prefix)
        // 3: category (optional, after category:)
        // 4: date (optional, after on:, accepts YYYY-MM-DD or today|yesterday|tomorrow)
        String regex = "^\\s*" +
                "(.*?)" +                          // 1: description (lazily up to amount)
                "\\s*\\$?(\\d+(?:\\.\\d{1,2})?)" + // 2: amount
                "(?:.*?\\bcategory:([A-Za-z0-9_]+))?" +  // 3: category
                "(?:.*?\\bon:((?:\\d{4}-\\d{2}-\\d{2})|today|yesterday|tomorrow))?" + // 4: date
                "\\s*$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Input must include a description and amount, e.g., 'coffee $5.50 category:food on:2024-01-01'.");
        }

        String description = matcher.group(1) != null ? matcher.group(1).trim() : "";
        String amountStr = matcher.group(2);
        String category = matcher.group(3) != null ? matcher.group(3).trim() : defaultCategory;
        String dateTag = matcher.group(4);

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format.");
        }

        LocalDate date = LocalDate.now();
        if (dateTag != null) {
            try {
                switch (dateTag.toLowerCase()) {
                    case "today":
                        date = LocalDate.now();
                        break;
                    case "yesterday":
                        date = LocalDate.now().minusDays(1);
                        break;
                    case "tomorrow":
                        date = LocalDate.now().plusDays(1);
                        break;
                    default:
                        date = LocalDate.parse(dateTag);
                        break;
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD, today, yesterday, or tomorrow.");
            }
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }

        Transaction t = new Transaction(type, amount, date, category, description);
        model.addTransaction(t);
    }

    /**
     * Requires: id is a non-null TransactionID.
     * Effects: Deletes the transaction from the model.
     * @param id The ID of the transaction to delete.
     */
    public void deleteTransaction(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID is required.");
        }

        String trimmed = id.trim();

        // If a full UUID is provided, try direct delete first
        try {
            if (trimmed.length() >= 36) {
                if (model.deleteTransaction(new model.TransactionID(trimmed))) {
                    return;
                }
            }
        } catch (IllegalArgumentException ignored) {
            // fall through to prefix resolution
        }

        // Fallback: resolve by prefix (short ID as shown in the UI)
        model.TransactionID resolved = model.resolveIdByPrefix(trimmed);
        if (resolved == null) {
            throw new IllegalArgumentException("Ambiguous or unknown Transaction ID prefix.");
        }
        model.deleteTransaction(resolved);
    }

    /**
     * Requires: newLimit >= 0, period is not null.
     * Effects: Updates the budget settings in the model.
     */
    public void setBudget(double newLimit, model.Budget.Period period) {
        model.getBudget().setLimit(newLimit);
        model.getBudget().setPeriod(period);
    }

    /**
     * Requires: idPrefix refers to a unique transaction; fields validated similarly to addTransactionExplicit
     * Effects: Updates matching transaction's fields and saves it back to the model
     */
    public void editTransactionByPrefix(String idPrefix, String description, Double amount, String category, String dateStr, Transaction.Type type) {
        if (idPrefix == null || idPrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID prefix is required.");
        }
        model.TransactionID resolved = model.resolveIdByPrefix(idPrefix.trim());
        if (resolved == null) {
            throw new IllegalArgumentException("Ambiguous or unknown Transaction ID prefix.");
        }
        Transaction existing = model.getTransactionById(resolved);
        if (existing == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }

        // Apply edits if provided
        if (description != null && !description.trim().isEmpty()) {
            existing.setDescription(description.trim());
        }
        if (amount != null) {
            existing.setAmount(amount);
        }
        if (category != null && !category.trim().isEmpty()) {
            existing.setCategory(category.trim());
        }
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            String s = dateStr.trim().toLowerCase();
            try {
                switch (s) {
                    case "today": existing.setDate(java.time.LocalDate.now()); break;
                    case "yesterday": existing.setDate(java.time.LocalDate.now().minusDays(1)); break;
                    case "tomorrow": existing.setDate(java.time.LocalDate.now().plusDays(1)); break;
                    default: existing.setDate(java.time.LocalDate.parse(dateStr.trim()));
                }
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD, today, yesterday, or tomorrow.");
            }
        }
        if (type != null) {
            // Transaction type is final in current model; to change type we'd need to recreate.
            // For now, we ignore type change requests to keep invariants.
        }

        // Persist update via model API (overwrites same ID)
        model.updateTransaction(existing);
    }
}
