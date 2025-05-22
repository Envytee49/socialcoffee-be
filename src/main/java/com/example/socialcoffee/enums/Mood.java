package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum Mood {
    HAPPY("happy", "You're feeling happy today, huh? We'll mood you up even more!"),
    SAD("sad", "Feeling down? We're here to cheer you up."),
    SOCIAL("social", "In a social mood? Let’s connect you with great people!"),
    RELAXED("relaxed", "Chill vibes detected. Enjoy the peace."),
    STRESSED("stressed", "Tough day? Let us help you unwind.");
    private final String value;
    private final String message;

    Mood(final String value, final String message) {
        this.value = value;
        this.message = message;
    }
}
