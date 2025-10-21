# Expense Tracker - Product Requirements Document (PRD)

## Overview
The Expense Tracker is a Java desktop application built using the Model-View-Controller (MVC) architectural pattern. It provides comprehensive financial transaction management capabilities including expense and income tracking, budget management, and data persistence.

## Core Functionalities

### 1. Transaction Management
- **Add Transactions**: Users can add both expense and income transactions with description, amount, category, and date
- **View Transactions**: Display all transactions in chronological order (newest first)
- **Edit Transactions**: Modify existing transaction details (description, amount, category, date)
- **Delete Transactions**: Remove transactions using transaction ID or ID prefix
- **Transaction Filtering**: Filter transactions by type, category, and date range

### 2. Budget Management
- **Set Budget Limits**: Configure weekly or monthly spending limits
- **Budget Tracking**: Monitor spending against budget limits
- **Budget Warnings**: Visual alerts when approaching or exceeding budget limits
- **Period Management**: Support for both weekly and monthly budget periods

### 3. Financial Summaries
- **Category Summaries**: View spending breakdown by category
- **Date Range Analysis**: Analyze expenses and income within specific time periods
- **Monthly Reports**: Automatic monthly expense categorization
- **Net Income Calculation**: Track total income vs expenses

### 4. Data Persistence
- **Automatic Saving**: Data is automatically saved when the application closes
- **Data Loading**: Previous data is loaded when the application starts
- **Serialization**: Uses Java object serialization for data storage

## Technical Implementation Status

| Requirement | Implementation Status | Details |
|-------------|----------------------|---------|
| **Architectural Pattern** | ✅ **IMPLEMENTED** | MVC pattern strictly followed with clear separation of concerns |
| **Design Patterns** | ✅ **IMPLEMENTED** | Observer Pattern and Singleton Pattern implemented |
| **Abstract Data Types (ADTs)** | ✅ **IMPLEMENTED** | Ledger and TransactionID implemented as significant ADTs |
| **Specifications & Design** | ✅ **IMPLEMENTED** | Complete Javadoc with @param, @return, @requires, @effects |
| **Representation Invariant** | ✅ **IMPLEMENTED** | Ledger class has documented RI and checkRep() method |
| **Mutability & Immutability** | ✅ **IMPLEMENTED** | TransactionID is immutable, Transaction is mutable |
| **Equality Implementation** | ✅ **IMPLEMENTED** | TransactionID implements equals() and hashCode() |
| **Regular Expressions** | ✅ **IMPLEMENTED** | Complex transaction parsing using regex patterns |

## Detailed Technical Requirements Analysis

### 1. Architectural Pattern: Model-View-Controller (MVC)
**Status: ✅ FULLY IMPLEMENTED**

- **Model**: `Ledger` class manages all transaction data and business logic
- **View**: `ExpenseView` class handles all UI components and user interactions
- **Controller**: `ExpenseController` class mediates between Model and View
- **Separation**: Strict separation maintained - no direct Model-View communication

### 2. Design Patterns Implementation

#### Observer Pattern
**Status: ✅ FULLY IMPLEMENTED**
- **Subject**: `Ledger` class implements observer management
- **Observer**: `IObserver` interface and `ExpenseView` implementation
- **Notification**: Automatic UI updates when data changes
- **Justification**: Essential for maintaining UI consistency with data changes

#### Singleton Pattern
**Status: ✅ FULLY IMPLEMENTED**
- **Implementation**: `PersistenceManager` class
- **Purpose**: Ensures single instance for data persistence operations
- **Justification**: Prevents multiple persistence managers and ensures consistent data handling

### 3. Abstract Data Types (ADTs)

#### Ledger ADT
**Status: ✅ FULLY IMPLEMENTED**
- **Purpose**: Central data structure managing all transactions
- **Operations**: Add, delete, update, query transactions
- **Representation Invariant**: Documented and enforced with checkRep()
- **Abstraction Function**: Documented mapping from internal representation to abstract concept

#### TransactionID ADT
**Status: ✅ FULLY IMPLEMENTED**
- **Purpose**: Unique identifier for transactions
- **Immutability**: Immutable design prevents ID modification
- **Equality**: Proper equals() and hashCode() implementation for Map usage

### 4. Specifications & Design
**Status: ✅ FULLY IMPLEMENTED**

All public methods in Model package include:
- `@param` annotations for all parameters
- `@return` annotations describing return values
- `@requires` annotations for preconditions
- `@effects` annotations for postconditions

### 5. Representation Invariant (RI) and Abstraction Function (AF)
**Status: ✅ FULLY IMPLEMENTED**

**Ledger RI**: "The internal collection of transactions cannot contain any null elements (keys or values), and every Transaction must have a unique TransactionID. All Transaction amounts must be strictly positive."

**Ledger AF**: "Represents a user's chronological financial history, where the internal Map<TransactionID, Transaction> maps unique transaction identifiers to their corresponding Transaction objects."

**checkRep() Method**: Implemented and called after all state-changing operations.

### 6. Mutability & Immutability Design Decisions
**Status: ✅ FULLY IMPLEMENTED**

- **TransactionID**: Immutable - prevents ID changes after creation
- **Transaction**: Mutable - allows editing of transaction details
- **Budget**: Mutable - allows budget limit and period changes
- **Ledger**: Mutable - allows adding/removing transactions

### 7. Equality Implementation
**Status: ✅ FULLY IMPLEMENTED**

**TransactionID** implements proper equals() and hashCode():
- **Purpose**: Enables reliable usage as Map keys in Ledger
- **Implementation**: Uses Objects.equals() and Objects.hash()
- **Consistency**: Maintains equals/hashCode contract

### 8. Regular Expressions Usage
**Status: ✅ FULLY IMPLEMENTED**

**Complex Transaction Parsing** in `ExpenseController.addTransactionFromInput()`:
- **Pattern**: Parses natural language input like "coffee $5.50 category:food on:yesterday"
- **Groups**: Captures description, amount, category, and date
- **Validation**: Ensures proper format and data integrity
- **Non-trivial**: Core feature for user-friendly transaction entry

## File Structure Analysis

### Model Package (`src/model/`)
- **Ledger.java**: Main data model implementing Observer pattern
- **Transaction.java**: Transaction entity with mutable fields
- **TransactionID.java**: Immutable unique identifier
- **Budget.java**: Budget management with period tracking
- **PersistenceManager.java**: Singleton for data persistence

### Controller Package (`src/controller/`)
- **ExpenseController.java**: MVC controller with input validation and regex parsing

### View Package (`src/view/`)
- **ExpenseView.java**: Main GUI implementing Observer pattern
- **IObserver.java**: Observer interface for UI updates

### Main Application
- **Main.java**: Application entry point setting up MVC structure

## Integration Architecture

### Data Flow
1. **User Input** → Controller validates and processes
2. **Controller** → Model updates data and notifies observers
3. **Model** → View receives notification and refreshes display
4. **Persistence** → Automatic saving/loading via Singleton manager

### Key Integration Points
- **Observer Pattern**: Ensures UI consistency with data changes
- **Singleton Pattern**: Manages persistence operations consistently
- **MVC Separation**: Clean architecture with defined responsibilities
- **Serialization**: Seamless data persistence across application sessions

## Conclusion

The Expense Tracker application successfully implements all required technical and design requirements:

✅ **MVC Architecture**: Strictly maintained with clear separation
✅ **Design Patterns**: Observer and Singleton patterns properly implemented
✅ **ADTs**: Ledger and TransactionID as significant abstract data types
✅ **Documentation**: Complete Javadoc specifications throughout
✅ **RI/AF**: Representation Invariant and Abstraction Function documented
✅ **Mutability Design**: Conscious decisions with proper justification
✅ **Equality**: TransactionID properly implements equals/hashCode
✅ **Regular Expressions**: Complex parsing for natural language input

The application provides a robust, well-architected solution for personal expense tracking with comprehensive functionality and proper software engineering practices.
