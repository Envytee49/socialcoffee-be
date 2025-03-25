package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.dto.common.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class GenerateResponse {
    private List<Choice> choices;
    @Getter
    @Setter
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
