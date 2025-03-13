//package com.example.socialcoffee.service;
//
//import com.example.socialcoffee.dto.request.ChatRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RecommendationService {
//    @Autowired
//    private ChatClient chatClient;
//
//    public String chat(ChatRequest chatRequest) {
//        PromptTemplate pt = new PromptTemplate(String.format("Generate a description from these parameter: name %s vibe %s service %s", "Cheess Coffee", "fancy", "Good service" ));
//        return chatClient.prompt(String.format("Generate a description from these parameter: name %s vibe %s service %s", "Cheess Coffee", "fancy", "Good service" )
//        ).call().content();
//    }
//}
