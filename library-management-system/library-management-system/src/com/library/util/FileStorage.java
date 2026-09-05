package com.library.util;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.MembershipType;
import com.library.model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles saving and loading library data to simple pipe-delimited text
 * files, so data survives between runs without needing a real database.
 */
public class FileStorage {
    private static final String DATA_DIR = "data";
    private static final String BOOKS_FILE = DATA_DIR + "/books.txt";
    private static final String MEMBERS_FILE = DATA_DIR + "/members.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.txt";
    private static final String SPLIT_REGEX = "\\|";
    private static final String JOIN_DELIMITER = "|";

    public static void ensureDataDirExists() throws IOException {
        Path path = Paths.get(DATA_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public static void saveBooks(Collection<Book> books) throws IOException {
        ensureDataDirExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                writer.write(String.join(JOIN_DELIMITER,
                        b.getBookId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                        String.valueOf(b.getTotalCopies()), String.valueOf(b.getAvailableCopies())));
                writer.newLine();
            }
        }
    }

    public static List<Book> loadBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        Path path = Paths.get(BOOKS_FILE);
        if (!Files.exists(path)) return books;

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(SPLIT_REGEX);
                books.add(new Book(parts[0], parts[1], parts[2], parts[3],
                        Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));
            }
        }
        return books;
    }

    public static void saveMembers(Collection<Member> members) throws IOException {
        ensureDataDirExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members) {
                writer.write(String.join(JOIN_DELIMITER,
                        m.getMemberId(), m.getName(), m.getEmail(), m.getMembershipType().name(),
                        String.join(",", m.getIssuedBookIds())));
                writer.newLine();
            }
        }
    }

    public static List<Member> loadMembers() throws IOException {
        List<Member> members = new ArrayList<>();
        Path path = Paths.get(MEMBERS_FILE);
        if (!Files.exists(path)) return members;

        try (BufferedReader reader = new BufferedReader(new FileReader(MEMBERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(SPLIT_REGEX, -1);
                Member m = new Member(parts[0], parts[1], parts[2], MembershipType.valueOf(parts[3]));
                if (parts.length > 4 && !parts[4].isEmpty()) {
                    for (String bookId : parts[4].split(",")) {
                        m.addIssuedBook(bookId);
                    }
                }
                members.add(m);
            }
        }
        return members;
    }

    public static void saveTransactions(Collection<Transaction> transactions) throws IOException {
        ensureDataDirExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : transactions) {
                writer.write(String.join(JOIN_DELIMITER,
                        t.getBookId(), t.getMemberId(), t.getIssueDate().toString(),
                        t.getDueDate().toString(),
                        t.isReturned() ? t.getReturnDate().toString() : "NULL",
                        String.valueOf(t.getFineAmount())));
                writer.newLine();
            }
        }
    }

    public static List<Transaction> loadTransactions() throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        Path path = Paths.get(TRANSACTIONS_FILE);
        if (!Files.exists(path)) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(SPLIT_REGEX, -1);
                Transaction t = new Transaction(parts[0], parts[1],
                        LocalDate.parse(parts[2]), LocalDate.parse(parts[3]));
                if (!parts[4].equals("NULL")) {
                    t.markReturned(LocalDate.parse(parts[4]), Double.parseDouble(parts[5]));
                }
                transactions.add(t);
            }
        }
        return transactions;
    }
}
