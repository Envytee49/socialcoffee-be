package com.example.socialcoffee.dto.common;

import lombok.Getter;

@Getter
public class Message {
    private String role;
    private String content;

    public Message(final String role,
                   final String content) {
        this.role = role;
        this.content = content;
    }
}
