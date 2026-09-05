package com.library.model;

import java.time.LocalDate;

public class Transaction {
    private String bookId;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null until the book is returned
    private double fineAmount;

    public Transaction(String bookId, String memberId, LocalDate issueDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineAmount = 0.0;
    }

    public String getBookId() { return bookId; }
    public String getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate returnDate, double fineAmount) {
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        String status = isReturned() ? "Returned on " + returnDate : "Due on " + dueDate;
        return String.format("Book %s | Member %s | Issued: %s | %s | Fine: %.2f",
                bookId, memberId, issueDate, status, fineAmount);
    }
}
