package com.ecstel.sym.utils;

import com.fasterxml.jackson.databind.JsonNode;

public class StringUtil {

    public static String getStringValue(String field) {
        if(field != null && !field.equals("null")){
            return field;
        }else{
            return "";
        }
    }
}
