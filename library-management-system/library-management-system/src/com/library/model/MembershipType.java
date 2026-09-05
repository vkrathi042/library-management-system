package com.library.model;

public enum MembershipType {
    STUDENT(3, 14),
    FACULTY(5, 30);

    private final int maxBooksAllowed;
    private final int loanPeriodDays;

    MembershipType(int maxBooksAllowed, int loanPeriodDays) {
        this.maxBooksAllowed = maxBooksAllowed;
        this.loanPeriodDays = loanPeriodDays;
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }
}
