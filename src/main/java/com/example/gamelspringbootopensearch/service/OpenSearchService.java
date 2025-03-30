package com.example.gamelspringbootopensearch.service;


import com.example.gamelspringbootopensearch.dto.ProductRegistrationMessage;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenSearchService {

    private final RestHighLevelClient openSearchClient;

    public OpenSearchService(RestHighLevelClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public void indexProduct(ProductRegistrationMessage message) {
        // 상품 등록 메시지를 기반으로 인덱싱할 데이터 구성
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", message.getName());
        jsonMap.put("description", message.getDescription());
        jsonMap.put("categoryId", message.getCategoryId());
        jsonMap.put("price", message.getPrice());
        jsonMap.put("hashtagIds", message.getHashtagIds());

        // "products" 인덱스에 문서를 생성/업데이트
        IndexRequest request = new IndexRequest("products")
                .id(String.valueOf(message.getProductId()))
                .source(jsonMap, XContentType.JSON);

        try {
            IndexResponse response = openSearchClient.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexing product in OpenSearch: " + response.getResult());
        } catch (IOException e) {
            // 예외 처리 로직 (필요에 따라 재시도, 로그 남기기 등 추가 가능)
            e.printStackTrace();
        }
    }
}