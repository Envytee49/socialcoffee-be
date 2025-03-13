package com.example.socialcoffee.dto.request;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record ChatRequest(@Nullable UUID chatId, String question) {}

