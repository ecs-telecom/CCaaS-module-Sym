package com.ecstel.sym.utils;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResponseData {
    private List<Map<String, Object>> data;

    public ResponseData(List<Map<String, Object>> data) {
        this.data = data;
    }
}