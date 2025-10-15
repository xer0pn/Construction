## **Expense Tracker Project Requirements and Design Constraints**

This document outlines the complete specifications for the Expense Tracker application, covering both the core functional features and the mandatory high-quality software construction principles.

### **1\. Functional Requirements (What the System Must Do)**

This table defines the specific features and capabilities the Expense Tracker application must possess.

| Feature Name | Description | Priority |
| :---- | :---- | :---- |
| **Persistent Data Storage** | Save all application data persistently between sessions (e.g., using Java Serialization). | High |
| **Validate Transaction Data** | Validate data before saving (must enforce correct format, non-empty fields, and positive amounts). | High |
| **Add Expense Transaction** | Allow users to add expense transactions with date, category, description, and amount. | High |
| **Add Income Transaction** | Allow users to add income transactions with date, description, and amount. | High |
| **Edit and Delete Transactions** | Allow users to modify or remove any existing transaction from the ledger. | High |
| **View Transaction List** | Enable users to view all transactions chronologically (newest first). | High |
| **Display Expense and Income Summary** | Display summaries of total expenses and incomes (by category and by date range). | High |
| **User Interface Views** | Provide separate, dedicated views for transactions, summaries, and budgets, as required for the MVC View layer. | High |
| **Set and Track Budget** | Enable users to set monthly/weekly budgets and track spending progress. | Medium |
| **Budget Warning Feedback** | Show visual feedback when spending approaches or exceeds the set budget limit. | Medium |
| **Filter Transactions** | Enable transactions to be filtered by category, date range, or type (expense/income). | Medium |
| **Manage Categories** | Allow categorization of expenses and creation of custom categories. | Medium |

### **2\. Technical and Design Requirements (How the System Must Be Built)**

These constraints are non-negotiable and govern the quality, structure, and internal implementation details of the Java code, demonstrating the application of SE-4111 course principles.

| Requirement Category | Description of Mandatory Implementation |
| :---- | :---- |
| **Architectural Pattern** | Your application must be built using the **Model-View-Controller (MVC)** pattern. You must maintain a strict, provable separation between data/logic (**Model**), user interface (**View**), and user-input handling (**Controller**). |
| **Design Patterns** | You must correctly implement at least **two** of the following: Observer, Facade, or Singleton. (For this project: **Observer** in the Model-View communication and **Singleton** for the Persistence Manager). |
| **Abstract Data Types (ADTs)** | You must design and implement at least two significant ADTs (e.g., **Transaction** and **Ledger**) that are central to your application's Model. |
| **Specifications & Design** | All public methods within your Model must have complete and formal **Javadoc specifications** (@param, @return, @requires, @effects). For one core ADT (e.g., Ledger), you must document its **Representation Invariant (RI)** and **Abstraction Function (AF)** and implement a private **checkRep()** method. |
| **Mutability & Immutability** | At least one of your core data types (e.g., **TransactionID**) must be **immutable**. You must justify conscious design decisions about the mutability of all other core classes (e.g., Budget is mutable). |
| **Equality** | You must correctly implement the **equals() and hashCode()** methods for the chosen immutable ADT (e.g., TransactionID), justifying its necessity for use as a reliable **Map key** in the Ledger. |
| **Recursion OR Regular Expressions/Grammars** | You must use either Recursion or **Regular Expressions** to solve a meaningful, non-trivial problem. (For this project: **Regular Expressions** are used in the Controller to parse complex natural language transaction input.) |

