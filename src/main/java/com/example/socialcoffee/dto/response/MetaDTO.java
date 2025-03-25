package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.enums.MetaData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDTO {
    private Integer code;
    private String message;

    public MetaDTO(MetaData metaData) {
        this.code = metaData.getMetaCode();
        this.message = metaData.getMessage();
    }
}
