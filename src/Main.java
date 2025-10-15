import controller.ExpenseController;
import model.Ledger;
import model.PersistenceManager;
import view.ExpenseView;
import javax.swing.SwingUtilities;

/**
 * Main Application Entry Point.
 * Sets up the MVC structure.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Initialize Model (Load from persistence)
        Ledger model = PersistenceManager.getInstance().loadLedger();

        // 2. Initialize Controller
        ExpenseController controller = new ExpenseController(model);

        // 3. Initialize View (must be done on the Event Dispatch Thread for Swing)
        SwingUtilities.invokeLater(() -> {
            new ExpenseView(model, controller);
        });

        // NOTE: The ExpenseController calls saveState() on a shutdown hook,
        // ensuring data is saved when the application is closed.
    }
}
