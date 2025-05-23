package com.example.socialcoffee.exception;

public class ActionNotAllowedException extends RuntimeException {
    public ActionNotAllowedException(final String message) {
        super(message);
    }

    public ActionNotAllowedException(final String message,
                                     final Throwable cause) {
        super(message,
                cause);
    }
}
