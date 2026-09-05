package com.library.model;

import java.util.Objects;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String bookId, String title, String author, String genre, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    // Used when reloading a book from the saved data file, where
    // availableCopies may differ from totalCopies.
    public Book(String bookId, String title, String author, String genre, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s by %-20s (%s) | Available: %d/%d",
                bookId, title, author, genre, availableCopies, totalCopies);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return bookId.equals(book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }
}
