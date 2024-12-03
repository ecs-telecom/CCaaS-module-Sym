package com.ecstel.sym.utils;


import com.ecstel.sym.vo.BatchInfo;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @Class Name : DataMap.java
 * @version 1.0
 * DB 리턴 받을때 사용하거나 Map 타입으로 사용할때 사용한다. (mybatis return 값 쉽게 파싱하기 위해서)
 */
public class DataMap extends LinkedHashMap<String, Object> implements Map<String, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMap.class);
    private static final long serialVersionUID = 1L;

    public static DataMap getNewDataMap() {
        return new DataMap();
    }

    public static DataMap getNewDataMap(HttpServletRequest request) {

        DataMap map = new DataMap();
        map.defaultSetInfo(request, map);

        return map;
    }

    public void setResult(String key, DataMap map, List<DataMap> list) {
        if (map != null) {
            map.put(key, list);
        }
    }

    @SuppressWarnings("unchecked")
    public List<DataMap> getResult(String key, DataMap map) {
        List<DataMap> list = new ArrayList<DataMap>();
        if (map != null && map.get(key) != null) {
            if (map.get(key) instanceof ArrayList) {
                list = (List<DataMap>) map.get(key);
            }
        }
        return list;
    }

    public void setResult(String key, List<DataMap> list) {
        this.put(key, list);
    }


    public List<DataMap> getResult(String key) {
        List<DataMap> list = new ArrayList<DataMap>();
        if (this.get(key) != null) {
            if (this.get(key) instanceof ArrayList) {
                list = (List<DataMap>) this.get(key);
            }
        }
        return list;
    }

    public List<DataMap> getList(String key) {
        List<DataMap> list = new ArrayList<DataMap>();
        if (this.get(key) != null) {
            if (this.get(key) instanceof ArrayList) {
                list = (List<DataMap>) this.get(key);
            }
        }
        
        return list;
    }

    public Object setList(String key, Long value) {
        return this.put(key, value);
    }

    public static DataMap makeParameterMap(JsonNode jsonNode, BatchInfo batchInfo) {
        DataMap resultMap = new DataMap();
        try{

            // JsonNode가 배열인 경우
            if (jsonNode.isArray()) {
                // 배열의 모든 요소를 추가
                List<DataMap> items = new ArrayList<>(); // 결과를 저장할 리스트

                for (int i = 0; i < jsonNode.size(); i++) {
                    JsonNode valueNode = jsonNode.get(i);
                    DataMap itemMap = new DataMap(); // 각 배열 요소에 대한 DataMap 생성

                    // 배열 요소가 객체인 경우
                    if (valueNode.isObject()) {
                        // 객체의 필드를 반복하여 DataMap에 추가
                        valueNode.fieldNames().forEachRemaining(fieldName -> {
                            JsonNode fieldValue = valueNode.get(fieldName);

                            // null 체크 후 값을 추가
                            if (fieldValue != null && !fieldValue.toString().equals("null")) {
                                itemMap.put(fieldName, StringUtil.getStringValue(fieldValue.asText()));
                            } else {
                                itemMap.put(fieldName, ""); // 또는 적절한 기본값 설정
                            }
                        });
                    } else {
                        // 배열 요소가 객체가 아닐 경우
                        itemMap.put("value", valueNode.asText());
                    }

                    // companyId를 각 요소에 추가
                    itemMap.put("companyId", batchInfo.getCompanyId());

                    // 완성된 itemMap을 리스트에 추가
                    items.add(itemMap);
                }

                // 최종 리스트를 DataMap에 추가
                resultMap.put("list", items);
            } else if (jsonNode.isObject()) {
                // JsonNode가 객체인 경우 필드를 반복하여 DataMap에 추가
                jsonNode.fieldNames().forEachRemaining(fieldName -> {
                    JsonNode fieldValue = jsonNode.get(fieldName);
                    // null 체크 후 값을 추가
                    if (fieldValue != null) {
                        resultMap.put(fieldName, fieldValue.toString());
                    } else {
                        resultMap.put(fieldName, null); // 또는 적절한 기본값 설정
                    }
                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }


        return resultMap;
    }
    public static DataMap makePrameterMap(HttpServletRequest request) {

        String reqURI = request.getRequestURI().toString();

        DataMap resultMap = new DataMap();

        int cnt = 0;
        Enumeration<?> reqParams = request.getParameterNames();
        while (reqParams.hasMoreElements()) {
            String keyName = (String) reqParams.nextElement();
            resultMap.put(keyName, request.getParameter(CustomStringUtil.nullConvert(keyName)));
            cnt++;
        }

        //defaultSetInfo(request, resultMap);

        return (DataMap) resultMap;
    }

    private static void defaultSetInfo(HttpServletRequest request, Map<String, Object> resultMap) {
        resultMap.put("contentType", request.getContentType());
        resultMap.put("contextPath", request.getContextPath());
        resultMap.put("uri", request.getRequestURI());
        resultMap.put("protocol", request.getProtocol());
        resultMap.put("ip", request.getRemoteAddr());
    }

    public static DataMap getParamMap(HttpServletRequest request) {
        return makePrameterMap(request);
    }

    private static String nvlTrim(String obj) {
        String result = "";
        if (obj != null)
            result = obj.trim();
        return result;
    }

    public Object get(String key) {
        Object o = null;
        try {
            o = super.get(key);
        } catch (NullPointerException e) {
            LOGGER.error("Error in get() key=" + key, e);
        }
        return o;
    }

    public String[] getStringArray(String key) {
        String[] r = null;
        try {
            if (super.get(key) instanceof String[]) {
                r = (String[]) super.get(key);
            } else {
                r = new String[1];
                r[0] = getString(key);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Error in getStringArray() key=" + key, e);
        }
        return r;
    }

    public String getString(String key) {
        String r = "";
        try {
            if (super.get(key) != null) {
                r = super.get(key).toString();
            }
        } catch (NullPointerException e) {
            LOGGER.error("Error in getString() key=" + key, e);
        }
        return r;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultInt) {
        int result = 0;
        String str = getString(key);
        try {
            if (StringUtils.isNotEmpty(str)) {
                result = Integer.valueOf(str);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Error in getInt() key=" + key + ", getString(key)==" + getString(key), e);
            result = defaultInt;
        }
        return result;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public long getLong(String key, long defaultLong) {
        long result = 0;
        String str = getString(key);
        try {
            if (StringUtils.isNotEmpty(str)) {
                result = Long.valueOf(str);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Error in getLong() key=" + key + ", getString(key)==" + getString(key), e);
            result = defaultLong;
        }
        return result;
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    public double getDouble(String key, double defaultDouble) {
        double result = 0;
        String str = getString(key);
        try {
            if (StringUtils.isNotEmpty(str)) {
                result = Double.valueOf(str);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Error in getDouble() key=" + key + ", getString(key)=" + getString(key), e);
            result = defaultDouble;
        }
        return result;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaltValue) {
        boolean result = defaltValue;
        String strObj = nvlTrim(key);
        if (strObj.length() > 0) {
            result = strObj.toLowerCase().equals("true");
        }
        return result;
    }

    public void setString(String key, String value) {
        super.put(key, value);
    }

    public void setInt(String key, int value) {
        super.put(key, value);
    }

    public void setInt(String key, String value) {
        super.put(key, Integer.valueOf(value));
    }

    public void setLong(String key, long value) {
        super.put(key, value);
    }

    public void setLong(String key, String value) {
        super.put(key, Long.valueOf(value));
    }

    public void setDouble(String key, double value) {
        super.put(key, value);
    }

    public void setDouble(String key, String value) {
        super.put(key, Double.valueOf(value));
    }

    public void setBoolean(String key, boolean value) {
        super.put(key, Boolean.valueOf(value));
    }

    public Object put(String key, Object value) {
        return super.put(CamelUtil.convert2CamelCase((String) key), value);
    }
}