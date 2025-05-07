package com.example.socialcoffee.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SentenceTransformerService {
    private final EmbeddingModel embeddingModel;

    public Float[] generateEmbeddingDescription(String description) {
        if (Objects.isNull(description)) return null;
        log.info("Start generating embedding description");
        final float[] embed = embeddingModel.embed(description);
        log.info("Finish generating embedding description");
        return ArrayUtils.toObject(embed);
    }
}
