package com.example.socialcoffee.service;

import com.example.socialcoffee.utils.StringAppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateTextService {
    private final ChatClient chatClient;
    public String parseFilterFromPrompt(String features, String prompt) {
        try {
            if (StringAppUtils.isEmpty(features)) return null;
            log.info("Start parseFilterFromPrompt");
            final String res = chatClient.prompt().user(prompt + features).call().content();
            log.info("Finish parseFilterFromPrompt");
            return res;
        } catch (Exception e) {
            log.error("FAILED generating description", e);
            throw new RuntimeException("FAILED generating description", e);
        }
    }
}
