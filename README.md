# Expense Tracker Application

A comprehensive Java desktop application for managing personal finances, built using the Model-View-Controller (MVC) architectural pattern.

## 🚀 Features

### Core Functionalities
- **Transaction Management**: Add, edit, delete, and view financial transactions
- **Budget Tracking**: Set weekly/monthly budget limits with visual warnings
- **Financial Summaries**: Category-based spending analysis and date range reports
- **Data Persistence**: Automatic saving and loading of financial data
- **User-Friendly Interface**: Intuitive GUI with tabbed navigation

### Transaction Types
- **Expenses**: Track outgoing money with categories
- **Income**: Record incoming money sources
- **Categories**: Organize transactions by custom categories
- **Date Management**: Support for specific dates or relative dates (today, yesterday, tomorrow)

## 🏗️ Architecture

### Model-View-Controller (MVC) Pattern
- **Model**: `Ledger` class manages all transaction data and business logic
- **View**: `ExpenseView` class handles GUI components and user interactions
- **Controller**: `ExpenseController` class mediates between Model and View

### Design Patterns Implemented
- **Observer Pattern**: Automatic UI updates when data changes
- **Singleton Pattern**: Ensures single instance for data persistence operations

## 📁 Project Structure

```
src/
├── Main.java                 # Application entry point
├── controller/
│   └── ExpenseController.java # MVC controller with input validation
├── model/
│   ├── Ledger.java          # Main data model (Subject in Observer pattern)
│   ├── Transaction.java      # Transaction entity with mutable fields
│   ├── TransactionID.java    # Immutable unique identifier
│   ├── Budget.java          # Budget management with period tracking
│   └── PersistenceManager.java # Singleton for data persistence
└── view/
    ├── ExpenseView.java      # Main GUI implementing Observer pattern
    └── IObserver.java       # Observer interface for UI updates
```

## 🔧 Key Components

### Ledger (Model)
- Central data structure managing all transactions
- Implements Observer pattern as Subject
- Provides transaction CRUD operations
- Calculates financial summaries and reports
- Enforces data integrity with Representation Invariant

### Transaction
- Represents individual financial transactions
- Contains: ID, type, amount, date, category, description
- Mutable design allows editing transaction details
- Validates data integrity (positive amounts, non-null fields)

### TransactionID
- Immutable unique identifier for transactions
- Uses UUID for global uniqueness
- Implements proper equals() and hashCode() for Map usage
- Prevents ID modification after creation

### Budget
- Manages user's budget settings
- Supports weekly and monthly periods
- Calculates period start/end dates
- Mutable to allow budget limit and period changes

### ExpenseController
- Handles user input validation
- Translates UI actions into Model commands
- Implements complex regex parsing for natural language input
- Manages transaction lifecycle operations

### ExpenseView
- Java Swing GUI with tabbed interface
- Implements Observer pattern for automatic updates
- Provides intuitive forms for data entry
- Displays filtered transaction lists and summaries

## 💡 Usage Examples

### Adding Transactions
```java
// Explicit method
controller.addTransactionExplicit("Coffee", 5.50, "Food", "today", Transaction.Type.EXPENSE);

// Natural language parsing
controller.addTransactionFromInput("coffee $5.50 category:food on:yesterday", 
                                  Transaction.Type.EXPENSE, "Food");
```

### Budget Management
```java
// Set monthly budget
controller.setBudget(1000.00, Budget.Period.MONTHLY);

// Set weekly budget
controller.setBudget(250.00, Budget.Period.WEEKLY);
```

### Transaction Operations
```java
// Delete by ID prefix
controller.deleteTransaction("abc12345");

// Edit transaction
controller.editTransactionByPrefix("abc12345", "Updated description", 
                                  null, "NewCategory", null, null);
```

## 🔍 Advanced Features

### Regular Expression Parsing
The application supports natural language transaction input:
- **Pattern**: `"coffee $5.50 category:food on:yesterday"`
- **Groups**: Captures description, amount, category, and date
- **Validation**: Ensures proper format and data integrity

### Data Integrity
- **Representation Invariant**: Enforced through checkRep() method
- **Validation**: All inputs validated for correctness
- **Immutability**: TransactionID prevents ID tampering
- **Consistency**: Observer pattern maintains UI-Model synchronization

### Financial Analysis
- **Category Summaries**: Spending breakdown by category
- **Date Range Analysis**: Custom period financial reports
- **Budget Tracking**: Visual warnings for overspending
- **Monthly Reports**: Automatic expense categorization

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- Java Swing (included with Java)

### Running the Application
1. Compile the Java files:
   ```bash
   javac -d bin src/*.java src/*/*.java
   ```

2. Run the application:
   ```bash
   java -cp bin Main
   ```

### Data Persistence
- Data is automatically saved to `expense_tracker_data.ser`
- Application loads previous data on startup
- Uses Java object serialization for data storage

## 📊 Technical Specifications

### Abstract Data Types (ADTs)
- **Ledger**: Central data structure with documented RI and AF
- **TransactionID**: Immutable identifier with proper equality implementation

### Documentation Standards
- Complete Javadoc with @param, @return, @requires, @effects
- Representation Invariant and Abstraction Function documented
- Comprehensive method specifications throughout Model package

### Design Decisions
- **Mutability**: Conscious decisions with proper justification
- **Equality**: TransactionID implements equals/hashCode for Map usage
- **Patterns**: Observer and Singleton patterns properly implemented

## 🎯 Key Benefits

1. **Clean Architecture**: Strict MVC separation with clear responsibilities
2. **Data Integrity**: Robust validation and invariant checking
3. **User Experience**: Intuitive interface with automatic updates
4. **Extensibility**: Well-designed ADTs and patterns for future enhancements
5. **Reliability**: Comprehensive error handling and data persistence


*Built with Java Swing, following MVC architecture and implementing Observer and Singleton design patterns.*
