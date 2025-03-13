package com.example.socialcoffee.dto.response;


import java.util.UUID;

public record ChatResponse(UUID chatId, String answer) {}
