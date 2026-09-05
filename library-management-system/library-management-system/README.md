# Library Management System

A console-based Library Management System built in core Java. It demonstrates OOP
design, collections, custom exception handling, and file-based data persistence —
no database or external framework required.

## Features
- Add books and register members (Student or Faculty, each with different
  borrowing limits and loan periods)
- Issue and return books, with automatic due-date tracking and late-fine calculation
- Search books by title or author
- View all books (sorted alphabetically) and all registered members
- View a member's full borrowing history
- Data persists between runs — saved automatically to text files in `data/`

## Concepts Demonstrated
- **OOP**: encapsulation, enums with behavior (`MembershipType`), model classes
  (`Book`, `Member`, `Transaction`)
- **Collections**: `HashMap` for fast lookups, `ArrayList`, custom sorting with
  `Comparator`
- **Exception handling**: custom checked exceptions for real-world error cases
  (book not found, no copies available, membership limit reached)
- **File I/O**: reading/writing plain text files for persistence
- **java.time**: due dates and overdue-fine calculation

## Project Structure
```
src/com/library/
├── Main.java                    — console menu and program entry point
├── model/
│   ├── Book.java
│   ├── Member.java
│   ├── MembershipType.java      — enum: borrowing limit + loan period per type
│   └── Transaction.java
├── exception/
│   ├── BookNotFoundException.java
│   ├── BookNotAvailableException.java
│   ├── MemberNotFoundException.java
│   └── MembershipLimitExceededException.java
├── service/
│   └── LibraryService.java      — core business logic
└── util/
    └── FileStorage.java         — save/load to data/*.txt
```

## How to Compile and Run
From the project's root folder:
```
javac -d out $(find src -name "*.java")
java -cp out com.library.Main
```

On Windows (if `find` isn't available), compile with:
```
javac -d out src\com\library\*.java src\com\library\model\*.java src\com\library\exception\*.java src\com\library\service\*.java src\com\library\util\*.java
java -cp out com.library.Main
```

## Business Rules
- Students can borrow up to 3 books at a time, with a 14-day loan period.
- Faculty can borrow up to 5 books at a time, with a 30-day loan period.
- Late returns are fined at 5 (currency-agnostic units) per day overdue.

## Possible Extensions
- Swap the text-file storage for a real database (JDBC + MySQL/SQLite)
- Add a JavaFX or Swing GUI on top of the existing `LibraryService`
- Add book categories/reservations, or email notifications for due dates
- Move to CSV with a proper library (e.g., OpenCSV) to safely handle commas
  or special characters in titles
