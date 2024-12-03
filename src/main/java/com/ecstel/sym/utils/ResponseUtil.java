package com.ecstel.sym.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<Map<String, Object>> Response(Object  responseData, Object  responseHeader) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", responseData);
        response.put("header", responseHeader);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

