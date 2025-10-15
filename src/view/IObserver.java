package view;

/**
 * Design Pattern: Observer Interface
 * Used by all View components that need to be notified when the Model (Ledger) changes.
 */
public interface IObserver {
    /**
     * Requires: True
     * Effects: Called by the Subject (Ledger) to notify the Observer that the Model's state has changed.
     * The Observer should then query the Ledger for updated information and refresh its display.
     */
    void update();
}
