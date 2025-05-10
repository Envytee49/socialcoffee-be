package com.example.socialcoffee.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDtoOut<T> {
    private int page;
    private int size;
    private long totalPages;
    private long totalElements;
    private Object metaData;
    private List<T> data;
    public static <T> PageDtoOut<T> from(int page, int size, long totalElements, List<T> data) {
        long totalPages = totalElements / size;
        if (totalElements % size != 0) {
            totalPages++;
        }
        return PageDtoOut.<T>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .data(data)
                .build();
    }
}
