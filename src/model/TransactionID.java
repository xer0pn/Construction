package model;

import java.util.Objects;
import java.util.UUID;

/**
 * ADT: Represents a unique, immutable identifier for a Transaction.
 *
 * This class is designed to be immutable, ensuring that the identity of a Transaction
 * cannot be changed once created. It correctly implements equals() and hashCode()
 * so that TransactionID objects can be reliably used as keys in a Map,
 * which is a core requirement for the Ledger's internal representation.
 */
public final class TransactionID {
    private final String id;

    /**
     * Requires: True (no preconditions)
     * Effects: Constructs a new TransactionID with a randomly generated UUID string.
     */
    public TransactionID() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Requires: id is a non-null string.
     * Effects: Constructs a TransactionID from a pre-existing string (used for loading from storage).
     * @param id the string representation of the ID
     */
    public TransactionID(String id) {
        if (id == null) {
            throw new IllegalArgumentException("TransactionID string cannot be null.");
        }
        this.id = id;
    }

    /**
     * Requires: True
     * Effects: Returns the string representation of this ID.
     * @return the string representation of this TransactionID
     */
    public String getID() {
        return id;
    }

    /**
     * Requires: True
     * Effects: Compares this TransactionID to the specified object. Returns true if and only if
     * the argument is a TransactionID object and contains the same internal ID string.
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        // Must implement equals() and hashCode() for reliable Map key usage (required by project)
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionID that = (TransactionID) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Requires: True
     * Effects: Returns a hash code value for the object, consistent with equals().
     * @return the hash code value for this TransactionID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Requires: True
     * Effects: Returns a string representation of the TransactionID.
     * @return the string representation of this TransactionID
     */
    @Override
    public String toString() {
        return id;
    }
}
