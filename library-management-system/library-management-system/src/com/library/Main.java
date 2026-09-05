package com.library;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.MemberNotFoundException;
import com.library.exception.MembershipLimitExceededException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.MembershipType;
import com.library.model.Transaction;
import com.library.service.LibraryService;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static LibraryService service = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            service.loadData();
        } catch (Exception e) {
            System.out.println("Starting with a fresh library (no saved data found).");
        }

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": addBook(); break;
                case "2": registerMember(); break;
                case "3": issueBook(); break;
                case "4": returnBook(); break;
                case "5": searchBooks(); break;
                case "6": viewAllBooks(); break;
                case "7": viewAllMembers(); break;
                case "8": viewMemberHistory(); break;
                case "9":
                    running = false;
                    saveAndExit();
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Book");
        System.out.println("2. Register Member");
        System.out.println("3. Issue Book");
        System.out.println("4. Return Book");
        System.out.println("5. Search Books");
        System.out.println("6. View All Books");
        System.out.println("7. View All Members");
        System.out.println("8. View Member History");
        System.out.println("9. Save & Exit");
        System.out.print("Choose an option: ");
    }

    private static void addBook() {
        System.out.print("Book ID: ");
        String id = scanner.nextLine().trim();
        if (service.bookExists(id)) {
            System.out.println("A book with this ID already exists.");
            return;
        }
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Number of copies: ");
        int copies = readInt();

        service.addBook(new Book(id, title, author, genre, copies));
        System.out.println("Book added successfully.");
    }

    private static void registerMember() {
        System.out.print("Member ID: ");
        String id = scanner.nextLine().trim();
        if (service.memberExists(id)) {
            System.out.println("A member with this ID already exists.");
            return;
        }
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Membership type (STUDENT/FACULTY): ");
        String typeStr = scanner.nextLine().trim().toUpperCase();

        MembershipType type;
        try {
            type = MembershipType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type entered, defaulting to STUDENT.");
            type = MembershipType.STUDENT;
        }

        service.registerMember(new Member(id, name, email, type));
        System.out.println("Member registered successfully.");
    }

    private static void issueBook() {
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine().trim();

        try {
            service.issueBook(memberId, bookId);
            System.out.println("Book issued successfully.");
        } catch (BookNotFoundException | MemberNotFoundException
                 | BookNotAvailableException | MembershipLimitExceededException e) {
            System.out.println("Could not issue book: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Book ID: ");
        String bookId = scanner.nextLine().trim();

        try {
            double fine = service.returnBook(memberId, bookId);
            if (fine > 0) {
                System.out.printf("Book returned. Late fine: %.2f%n", fine);
            } else {
                System.out.println("Book returned on time. No fine.");
            }
        } catch (BookNotFoundException | MemberNotFoundException e) {
            System.out.println("Could not return book: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void searchBooks() {
        System.out.print("Search by (1) Title or (2) Author: ");
        String type = scanner.nextLine().trim();
        System.out.print("Keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Book> results = type.equals("2")
                ? service.searchByAuthor(keyword)
                : service.searchByTitle(keyword);

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            for (Book b : results) {
                System.out.println(b);
            }
        }
    }

    private static void viewAllBooks() {
        List<Book> allBooks = service.getAllBooksSortedByTitle();
        if (allBooks.isEmpty()) {
            System.out.println("No books in the library yet.");
        } else {
            for (Book b : allBooks) {
                System.out.println(b);
            }
        }
    }

    private static void viewAllMembers() {
        List<Member> allMembers = service.getAllMembers();
        if (allMembers.isEmpty()) {
            System.out.println("No members registered yet.");
        } else {
            for (Member m : allMembers) {
                System.out.println(m);
            }
        }
    }

    private static void viewMemberHistory() {
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        List<Transaction> history = service.getMemberHistory(memberId);
        if (history.isEmpty()) {
            System.out.println("No transaction history for this member.");
        } else {
            for (Transaction t : history) {
                System.out.println(t);
            }
        }
    }

    private static void saveAndExit() {
        try {
            service.saveData();
            System.out.println("Data saved. Goodbye!");
        } catch (Exception e) {
            System.out.println("Warning: could not save data - " + e.getMessage());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
