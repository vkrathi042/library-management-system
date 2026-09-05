package com.library.exception;

public class MembershipLimitExceededException extends Exception {
    public MembershipLimitExceededException(String message) {
        super(message);
    }
}
