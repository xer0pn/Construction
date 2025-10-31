package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * ADT: Represents the user's budget settings.
 * This is a MUTABLE type as its state is expected to change based on user actions.
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private double limit; // The budget amount
    private Period period; // Weekly or Monthly

    // --- Representation Invariant (RI) ---
    /*
     * RI: "The limit field must be non-negative (limit >= 0). The period field is non-null
     * and must be one of the valid Period enum values (WEEKLY or MONTHLY)."
     */

    // --- Abstraction Function (AF) ---
    /*
     * AF(r) = "Represents the user's budget configuration for expense tracking, where the
     * internal representation (r) consists of a non-negative monetary limit (limit) that
     * defines the maximum spending amount, and a time period (period) that specifies whether
     * the budget is tracked on a weekly or monthly basis. The budget settings can be modified
     * by the user to adjust spending limits and tracking periods."
     */

    /**
     * Requires: True
     * Effects: Constructs a default budget (e.g., $1000 monthly).
     */
    public Budget() {
        this.limit = 1000.00;
        this.period = Period.MONTHLY;
        checkRep(); // Check RI after construction
    }

    /**
     * Requires: The RI holds true.
     * Effects: Asserts that the Representation Invariant holds true.
     * Will throw an AssertionError if the RI is violated.
     */
    private void checkRep() {
        assert limit >= 0 : "Budget limit must be non-negative.";
        assert period != null : "Budget period cannot be null.";
    }

    /**
     * Requires: newLimit >= 0
     * Effects: Sets the new budget limit.
     * @param newLimit the new budget limit (must be non-negative)
     */
    public void setLimit(double newLimit) {
        if (newLimit < 0) {
            throw new IllegalArgumentException("Budget limit cannot be negative.");
        }
        this.limit = newLimit;
        checkRep(); // Check RI after mutation
    }

    /**
     * Requires: newPeriod is not null
     * Effects: Sets the budget tracking period.
     * @param newPeriod the new budget period (WEEKLY or MONTHLY)
     */
    public void setPeriod(Period newPeriod) {
        if (newPeriod == null) {
            throw new IllegalArgumentException("Period cannot be null.");
        }
        this.period = newPeriod;
        checkRep(); // Check RI after mutation
    }

    /**
     * Requires: True
     * Effects: Returns the budget limit.
     * @return the current budget limit
     */
    public double getLimit() {
        return limit;
    }

    /**
     * Requires: True
     * Effects: Returns the budget period.
     * @return the current budget period
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Requires: date is the current date.
     * Effects: Calculates and returns the start date of the current budget period.
     * @param date the date to determine the period start
     * @return the start date of the current budget period
     */
    public LocalDate getPeriodStartDate(LocalDate date) {
        if (period == Period.MONTHLY) {
            return date.withDayOfMonth(1);
        } else {
            // WEEKLY (starts on Monday by default)
            return date.with(DayOfWeek.MONDAY);
        }
    }

    /**
     * Requires: date is the current date.
     * Effects: Calculates and returns the end date of the current budget period.
     * @param date the date to determine the period end
     * @return the end date of the current budget period
     */
    public LocalDate getPeriodEndDate(LocalDate date) {
        if (period == Period.MONTHLY) {
            return date.withDayOfMonth(date.lengthOfMonth());
        } else {
            // WEEKLY (ends on Sunday by default)
            return date.with(DayOfWeek.SUNDAY);
        }
    }

    public enum Period {
        WEEKLY,
        MONTHLY
    }
}
