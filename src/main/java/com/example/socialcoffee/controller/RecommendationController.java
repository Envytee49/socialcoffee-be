//package com.example.socialcoffee.controller;
//
//import com.example.socialcoffee.dto.request.ChatRequest;
//import com.example.socialcoffee.dto.response.ChatResponse;
//import com.example.socialcoffee.service.RecommendationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/recommendation")
//public class RecommendationController {
//    @Autowired
//    private RecommendationService recommendationService;
//    @GetMapping
//    public ResponseEntity<String> getRecommendation(@RequestBody ChatRequest chatRequest) {
//        String chatResponse = recommendationService.chat(chatRequest);
//        return ResponseEntity.ok(chatResponse);
//    }
//}
