package com.example.socialcoffee.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
    }

    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final String message,
                                 final Throwable cause) {
        super(message,
                cause);
    }
}
