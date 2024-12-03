package com.ecstel.sym.auth;

import com.ecstel.sym.vo.BatchInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component // Spring 컴포넌트로 등록
public class AuthTokenInitializer {

    private static JsonNode tokenInfo; // 토큰 정보를 저장할 인스턴스 변수 추가

    public static JsonNode getTokenInfo() { // 토큰 정보를 가져오는 메서드 추가
        return tokenInfo;
    }

    public static int initAuthToken(BatchInfo batchInfo) throws IOException { // 반환 타입을 String으로 변경
        String url = batchInfo.getGlobalProductionUrl() + "/auth/token";
        String clientIdAndSecretBase64 = "YWY3MDJjNTUtMDhkZC00MTM1LTkyMmQtM2IwMzM3ZWYyODA3Onl3L0gxRWpINUE3VVBqeHpkNlpqZlE9PQ==";
        String clientId = batchInfo.getClientId();
        String clientSecret = batchInfo.getClientSecret();
        String grantType = batchInfo.getGrantType();

        // URL 인코딩
        String data = String.format("grant_type=%s&username=%s&password=%s",
                URLEncoder.encode(grantType, StandardCharsets.UTF_8),
                URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                URLEncoder.encode(clientSecret, StandardCharsets.UTF_8));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", "Basic " + clientIdAndSecretBase64);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new StringEntity(data));

            HttpResponse response = httpClient.execute(httpPost);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            // JSON 응답 파싱 및 인스턴스 변수에 저장
            ObjectMapper objectMapper = new ObjectMapper();
            tokenInfo = objectMapper.readTree(jsonResponse); // 인스턴스 변수에 저장

            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;  // 에러를 호출한 곳으로 전달
        }
    }
}
