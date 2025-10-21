package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.Set; // REMOVED: Compiler indicated this import is unused.
import java.util.stream.Collectors;
import view.IObserver;
// import model.Budget; // REMOVED: Compiler indicated this import is unused.

/**
 * ADT: The central Model class (Subject in Observer Pattern).
 * Manages all Transaction data and provides summary calculations.
 * Implements Serializable for persistence.
 */
public class Ledger implements Serializable {
    private static final long serialVersionUID = 1L;

    // Internal Representation: A Map provides efficient look-up by the immutable TransactionID.
    private final Map<TransactionID, Transaction> transactions;
    private final List<IObserver> observers; // For Observer Pattern
    private final Budget budget; // For Budget tracking

    // --- Representation Invariant (RI) ---
    /*
     * RI: "The internal collection of transactions (the Map) cannot contain any null
     * elements (keys or values), and every Transaction must have a unique TransactionID.
     * All Transaction amounts must be strictly positive."
     */

    /**
     * Requires: True
     * Effects: Constructs an empty Ledger with a new Budget instance.
     */
    public Ledger() {
        this.transactions = new HashMap<>();
        this.observers = new ArrayList<>();
        this.budget = new Budget(); // Budget is part of the Model's state
        checkRep(); // Check RI after construction
    }

    /**
     * Requires: The RI holds true.
     * Effects: Iterates through the internal map and asserts that the RI holds true.
     * Will throw an AssertionError if the RI is violated.
     */
    private void checkRep() {
        assert transactions != null : "The transaction map cannot be null.";
        for (Map.Entry<TransactionID, Transaction> entry : transactions.entrySet()) {
            assert entry.getKey() != null : "TransactionID key cannot be null.";
            assert entry.getValue() != null : "Transaction value cannot be null.";
            assert entry.getKey().equals(entry.getValue().getId()) : "Key must match the Transaction's internal ID.";
            assert entry.getValue().getAmount() > 0 : "Transaction amount must be positive.";
        }
    }

    // --- Abstraction Function (AF) ---
    /*
     * AF(r) = "Represents a user's chronological financial history, where the internal
     * Map<TransactionID, Transaction> (r) maps unique transaction identifiers to their
     * corresponding Transaction objects, collectively forming the complete set of
     * financial records managed by the Expense Tracker."
     */

    // --- Core Data Operations ---

    /**
     * Requires: t is a valid Transaction (non-null, positive amount, non-null date, etc.).
     * Effects: Adds the given transaction to the ledger, and notifies all observers.
     * @param t the Transaction to add to the ledger
     */
    public void addTransaction(Transaction t) {
        if (transactions.containsKey(t.getId())) {
            // Should not happen with new TransactionIDs, but good check for defensive programming
            throw new IllegalArgumentException("Transaction ID already exists.");
        }
        transactions.put(t.getId(), t);
        checkRep();
        notifyObservers();
    }

    /**
     * Requires: id is a non-null TransactionID that exists in the ledger.
     * Effects: Removes the transaction associated with the given ID, and notifies all observers.
     * @param id the TransactionID of the transaction to delete
     * @return true if the transaction was successfully deleted, false if not found
     */
    public boolean deleteTransaction(TransactionID id) {
        if (transactions.remove(id) != null) {
            checkRep();
            notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * Requires: prefix is non-null and non-empty.
     * Effects: Resolves a TransactionID by its string prefix. If exactly one
     * matching ID exists, returns that TransactionID. If none or more than one
     * match is found, returns null to indicate an ambiguous or missing match.
     * @param prefix the string prefix to search for
     * @return the matching TransactionID or null if ambiguous/missing
     */
    public TransactionID resolveIdByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return null;
        }

        List<TransactionID> matches = new ArrayList<>();
        for (TransactionID tid : transactions.keySet()) {
            if (tid.getID().startsWith(prefix)) {
                matches.add(tid);
            }
        }

        return matches.size() == 1 ? matches.get(0) : null;
    }

    /**
     * Requires: t is a Transaction object whose ID exists in the ledger.
     * Effects: Replaces the existing transaction with the updated Transaction object t,
     * and notifies all observers.
     * @param t the updated Transaction object
     * @return true if the transaction was successfully updated, false if not found
     */
    public boolean updateTransaction(Transaction t) {
        if (transactions.containsKey(t.getId())) {
            transactions.put(t.getId(), t); // Overwrites the old object using the same key
            checkRep();
            notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * Requires: True
     * Effects: Returns an unmodifiable list of all transactions, sorted chronologically.
     * (Requirement: View Transaction List)
     * @return an unmodifiable list of all transactions sorted by date (newest first)
     */
    public List<Transaction> getAllTransactionsSorted() {
        List<Transaction> list = new ArrayList<>(transactions.values());
        // Sort in memory (as requested in constraints)
        Collections.sort(list, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        return Collections.unmodifiableList(list);
    }

    /**
     * Requires: True
     * Effects: Calculates the total amount for the given type and within the date range.
     * (Requirement: Display Expense and Income Summary)
     * @param type the transaction type (EXPENSE or INCOME)
     * @param start the start date of the range (inclusive)
     * @param end the end date of the range (inclusive)
     * @return the total amount for the specified type within the date range
     */
    public double calculateTotal(Transaction.Type type, LocalDate start, LocalDate end) {
        return transactions.values().stream()
                .filter(t -> t.getType() == type)
                .filter(t -> (t.getDate().isEqual(start) || t.getDate().isAfter(start)) &&
                             (t.getDate().isEqual(end) || t.getDate().isBefore(end)))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Requires: True
     * Effects: Returns a map of category names to their total expense amount for the current month.
     * (Requirement: Display Expense and Income Summary)
     * @param date the date to determine the month for the summary
     * @return a map of category names to their total expense amounts for the month
     */
    public Map<String, Double> getMonthlyExpenseSummaryByCategory(LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        return transactions.values().stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .filter(t -> t.getDate().isAfter(startOfMonth.minusDays(1)) && t.getDate().isBefore(endOfMonth.plusDays(1)))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    /**
     * Requires: start and end define an inclusive range; type non-null
     * Effects: Returns a map of category -> total amount for the given type within [start, end].
     * @param type the transaction type (EXPENSE or INCOME)
     * @param start the start date of the range (inclusive)
     * @param end the end date of the range (inclusive)
     * @return a map of category names to their total amounts for the specified type and date range
     */
    public Map<String, Double> getCategorySummaryByDateRange(Transaction.Type type, LocalDate start, LocalDate end) {
        return transactions.values().stream()
                .filter(t -> t.getType() == type)
                .filter(t -> (t.getDate().isEqual(start) || t.getDate().isAfter(start)) &&
                             (t.getDate().isEqual(end) || t.getDate().isBefore(end)))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    // --- Budget Management ---

    /**
     * Requires: True
     * Effects: Returns the Budget object.
     * @return the Budget object associated with this ledger
     */
    public Budget getBudget() {
        return budget;
    }

    // --- Observer Pattern Implementation (Subject) ---

    public void addObserver(IObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IObserver observer) {
        this.observers.remove(observer);
    }

    private void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Requires: id is not null
     * Effects: Returns the Transaction for the given ID, or null if not found.
     * @param id the TransactionID to search for
     * @return the Transaction with the given ID, or null if not found
     */
    public Transaction getTransactionById(TransactionID id) {
        return transactions.get(id);
    }

    /**
     * Requires: prefix is non-null/non-empty
     * Effects: Returns the Transaction matching the unique ID prefix, or null if none/ambiguous.
     * @param prefix the string prefix to search for
     * @return the Transaction matching the prefix, or null if ambiguous/missing
     */
    public Transaction getTransactionByPrefix(String prefix) {
        TransactionID resolved = resolveIdByPrefix(prefix);
        if (resolved == null) {
            return null;
        }
        return transactions.get(resolved);
    }
}
