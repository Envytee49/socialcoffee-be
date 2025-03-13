package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.dto.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class GenerateResponse {
    private List<Choice> choices;
    public static class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
