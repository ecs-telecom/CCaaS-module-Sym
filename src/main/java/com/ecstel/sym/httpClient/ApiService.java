package com.ecstel.sym.httpClient;

import com.ecstel.sym.auth.AuthTokenInitializer;
import com.ecstel.sym.vo.BatchInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ApiService {

    @Autowired
    private AuthTokenInitializer authTokenInitializer; // AuthTokenInitializer 주입

    @Autowired
    private BatchInfo batchInfo; // AuthTokenInitializer 주입

    private static final ObjectMapper objectMapper = new ObjectMapper(); // JSON 처리

    public CompletableFuture<JsonNode> doAction(String url, Map<String, String> params ,BatchInfo batchInfo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return makeApiCall(url, params,batchInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public JsonNode makeApiCall(String url, Map<String, String> params, BatchInfo batchInfo) throws Exception {
        JsonNode tokenInfo = authTokenInitializer.getTokenInfo();
        String accessToken = tokenInfo.path("access_token").asText();

        // URL과 파라미터를 조합
        URIBuilder uriBuilder = new URIBuilder(batchInfo.getCcTenantUrl() + url);
        if (params != null) {
            params.forEach(uriBuilder::addParameter);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Authorization", "Bearer " + accessToken);

            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                int ret = authTokenInitializer.initAuthToken(batchInfo);
                if (ret == 200) {
                    return makeApiCall(url, params, batchInfo);
                }
            }

            String jsonResponse = EntityUtils.toString(response.getEntity());
            return objectMapper.readTree(jsonResponse);
        }
    }
}