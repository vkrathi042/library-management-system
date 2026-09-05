package com.library.model;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String memberId;
    private String name;
    private String email;
    private MembershipType membershipType;
    private List<String> issuedBookIds;

    public Member(String memberId, String name, String email, MembershipType membershipType) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.membershipType = membershipType;
        this.issuedBookIds = new ArrayList<>();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public MembershipType getMembershipType() { return membershipType; }
    public List<String> getIssuedBookIds() { return issuedBookIds; }

    public boolean canIssueMoreBooks() {
        return issuedBookIds.size() < membershipType.getMaxBooksAllowed();
    }

    public void addIssuedBook(String bookId) {
        issuedBookIds.add(bookId);
    }

    public void removeIssuedBook(String bookId) {
        issuedBookIds.remove(bookId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | %-25s | %-8s | Books issued: %d/%d",
                memberId, name, email, membershipType,
                issuedBookIds.size(), membershipType.getMaxBooksAllowed());
    }
}
