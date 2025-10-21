package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Design Pattern: Singleton
 * Handles the persistent saving and loading of the Ledger object using Java Serialization.
 * (Requirement: Persistent Data Storage)
 */
public final class PersistenceManager {
    private static PersistenceManager instance; // The single instance
    private static final String FILE_PATH = "expense_tracker_data.ser"; // Serialization file

    /**
     * Requires: True
     * Effects: Private constructor to prevent direct instantiation.
     */
    private PersistenceManager() {
        // Private constructor
    }

    /**
     * Requires: True
     * Effects: Returns the single instance of the PersistenceManager (Lazy initialization).
     * @return the singleton instance of PersistenceManager
     */
    public static PersistenceManager getInstance() {
        // Singleton Implementation
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    /**
     * Requires: ledger is not null.
     * Effects: Serializes the Ledger object to the file path.
     * (Functional Requirement: Persistent Data Storage)
     * @param ledger the Ledger object to save
     */
    public void saveLedger(Ledger ledger) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(ledger);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving ledger: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Requires: True
     * Effects: Attempts to deserialize the Ledger object from the file path. Returns a new
     * Ledger if the file does not exist or an error occurs during loading.
     * @return the loaded Ledger object or a new Ledger if loading fails
     */
    public Ledger loadLedger() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No saved data found. Starting with a new ledger.");
            return new Ledger();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Ledger ledger = (Ledger) ois.readObject();
            System.out.println("Data loaded successfully.");
            return ledger;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading ledger. Creating new ledger. " + e.getMessage());
            // Fallback: Return a new Ledger on failure
            return new Ledger();
        }
    }
}
