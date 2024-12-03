package com.ecstel.sym.utils;

import lombok.Data;

import java.util.Map;

@Data
public class ResponseHeader {
    private Map<String, Object> header;

    public ResponseHeader(Map<String, Object> header) {
        this.header = header;
    }

    // getter, setter 생략
}
