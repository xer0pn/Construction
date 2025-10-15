package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * ADT: Represents a single financial transaction (Expense or Income).
 *
 * This class is mutable, as its description or category might need to be edited
 * by the user (High-Priority requirement: "Edit and Delete Transactions").
 * It implements Serializable for persistence.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TransactionID id; // Immutable ID
    private final Type type;
    private double amount;
    private LocalDate date;
    private String category;
    private String description;

    /**
     * Requires: amount > 0, date is not null, type is not null, category and description are non-null.
     * Effects: Constructs a new Transaction with a new unique ID.
     */
    public Transaction(Type type, double amount, LocalDate date, String category, String description) {
        // Input validation is mainly handled by the Controller, but basic checks are done here.
        if (amount <= 0 || date == null || type == null || category == null || description == null) {
             throw new IllegalArgumentException("Invalid transaction parameters.");
        }

        this.id = new TransactionID();
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    // --- Accessors (Getters) ---

    /**
     * Requires: True
     * Effects: Returns the unique, immutable ID of this transaction.
     */
    public TransactionID getId() {
        return id;
    }

    /**
     * Requires: True
     * Effects: Returns the type (EXPENSE or INCOME) of the transaction.
     */
    public Type getType() {
        return type;
    }

    /**
     * Requires: True
     * Effects: Returns the amount of the transaction.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Requires: True
     * Effects: Returns the date of the transaction.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Requires: True
     * Effects: Returns the category of the transaction.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Requires: True
     * Effects: Returns the description of the transaction.
     */
    public String getDescription() {
        return description;
    }

    // --- Mutators (Setters) ---

    /**
     * Requires: newAmount > 0 (Validation check required by project).
     * Effects: Sets the new amount for this transaction.
     */
    public void setAmount(double newAmount) {
        if (newAmount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        this.amount = newAmount;
    }

    /**
     * Requires: newDate is not null.
     * Effects: Sets the new date for this transaction.
     */
    public void setDate(LocalDate newDate) {
        if (newDate == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.date = newDate;
    }

    /**
     * Requires: newCategory is not null or empty.
     * Effects: Sets the new category for this transaction.
     */
    public void setCategory(String newCategory) {
        if (newCategory == null || newCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        this.category = newCategory;
    }

    /**
     * Requires: newDescription is not null or empty.
     * Effects: Sets the new description for this transaction.
     */
    public void setDescription(String newDescription) {
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        this.description = newDescription;
    }

    /**
     * Enum for transaction type.
     */
    public enum Type {
        EXPENSE,
        INCOME
    }
}
