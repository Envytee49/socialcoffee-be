package com.example.socialcoffee.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Message {
    private String role;
    private String content;

    public Message(final String role,
                   final String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
