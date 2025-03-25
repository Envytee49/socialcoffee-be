package com.example.socialcoffee.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class ResponseMetaData {
    @JsonProperty("meta")
    private List<MetaDTO> meta;

    @JsonProperty("data")
    private Object data;

    public ResponseMetaData(MetaDTO meta, Object data) {
        this.meta = Collections.singletonList(meta);
        this.data = data;
    }

    public ResponseMetaData(List<MetaDTO> meta, Object data) {
        this.meta = meta;
        this.data = data;
    }

    public ResponseMetaData(List<MetaDTO> meta) {
        this.meta = meta;
    }

    public ResponseMetaData(MetaDTO meta) {
        this.meta = Collections.singletonList(meta);
    }

    @JsonSetter
    public void setMeta(List<MetaDTO> meta) {
        this.meta = meta;
    }

    public void setMeta(MetaDTO meta) {
        this.meta = Collections.singletonList(meta);
    }

    public void setData(Object data) {
        this.data = data;
    }
}

