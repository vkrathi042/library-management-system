package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.MemberNotFoundException;
import com.library.exception.MembershipLimitExceededException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.util.FileStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Core business logic for the library: adding books/members, issuing and
 * returning books, searching, and fine calculation. Keeps all in-memory
 * state and delegates persistence to FileStorage.
 */
public class LibraryService {
    private Map<String, Book> books;
    private Map<String, Member> members;
    private List<Transaction> transactions;

    private static final double FINE_PER_DAY = 5.0;

    public LibraryService() {
        books = new LinkedHashMap<>();
        members = new LinkedHashMap<>();
        transactions = new ArrayList<>();
    }

    public void loadData() throws IOException {
        for (Book b : FileStorage.loadBooks()) {
            books.put(b.getBookId(), b);
        }
        for (Member m : FileStorage.loadMembers()) {
            members.put(m.getMemberId(), m);
        }
        transactions.addAll(FileStorage.loadTransactions());
    }

    public void saveData() throws IOException {
        FileStorage.saveBooks(books.values());
        FileStorage.saveMembers(members.values());
        FileStorage.saveTransactions(transactions);
    }

    public void addBook(Book book) {
        books.put(book.getBookId(), book);
    }

    public void registerMember(Member member) {
        members.put(member.getMemberId(), member);
    }

    public Book getBook(String bookId) throws BookNotFoundException {
        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("No book found with ID: " + bookId);
        }
        return book;
    }

    public Member getMember(String memberId) throws MemberNotFoundException {
        Member member = members.get(memberId);
        if (member == null) {
            throw new MemberNotFoundException("No member found with ID: " + memberId);
        }
        return member;
    }

    public void issueBook(String memberId, String bookId)
            throws BookNotFoundException, MemberNotFoundException,
            BookNotAvailableException, MembershipLimitExceededException {

        Book book = getBook(bookId);
        Member member = getMember(memberId);

        if (!book.isAvailable()) {
            throw new BookNotAvailableException("\"" + book.getTitle() + "\" has no copies available right now.");
        }
        if (!member.canIssueMoreBooks()) {
            throw new MembershipLimitExceededException(member.getName() + " has reached their limit of "
                    + member.getMembershipType().getMaxBooksAllowed() + " books.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        member.addIssuedBook(bookId);

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(member.getMembershipType().getLoanPeriodDays());
        transactions.add(new Transaction(bookId, memberId, issueDate, dueDate));
    }

    public double returnBook(String memberId, String bookId)
            throws BookNotFoundException, MemberNotFoundException {

        Book book = getBook(bookId);
        Member member = getMember(memberId);

        Transaction activeTransaction = null;
        for (Transaction t : transactions) {
            if (t.getBookId().equals(bookId) && t.getMemberId().equals(memberId) && !t.isReturned()) {
                activeTransaction = t;
                break;
            }
        }

        if (activeTransaction == null) {
            throw new IllegalStateException("No active loan found for this book and member.");
        }

        LocalDate today = LocalDate.now();
        long overdueDays = ChronoUnit.DAYS.between(activeTransaction.getDueDate(), today);
        double fine = overdueDays > 0 ? overdueDays * FINE_PER_DAY : 0.0;

        activeTransaction.markReturned(today, fine);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        member.removeIssuedBook(bookId);

        return fine;
    }

    public List<Book> searchByTitle(String keyword) {
        List<Book> result = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Book> searchByAuthor(String keyword) {
        List<Book> result = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Book> getAllBooksSortedByTitle() {
        List<Book> result = new ArrayList<>(books.values());
        result.sort(Comparator.comparing(Book::getTitle));
        return result;
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    public List<Transaction> getMemberHistory(String memberId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getMemberId().equals(memberId)) {
                result.add(t);
            }
        }
        return result;
    }

    public boolean bookExists(String bookId) {
        return books.containsKey(bookId);
    }

    public boolean memberExists(String memberId) {
        return members.containsKey(memberId);
    }
}
