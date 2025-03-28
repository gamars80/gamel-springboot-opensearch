package com.example.gamelspringbootopensearch.service;

import com.example.gamelspringbootopensearch.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final RestHighLevelClient client;
    private static final String INDEX = "products";

    public List<ProductDTO> searchProducts(Long categoryId, List<Long> hashtagIds) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("categoryId", categoryId));
        hashtagIds.forEach(tag -> query.must(QueryBuilders.termQuery("hashtagIds", tag)));

        sourceBuilder.query(query);
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        List<ProductDTO> results = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> src = hit.getSourceAsMap();
            ProductDTO dto = ProductDTO.builder()
                    .id(Long.valueOf(src.get("id").toString()))
                    .name(src.get("name").toString())
                    .description(src.get("description").toString())
                    .categoryId(Long.valueOf(src.get("categoryId").toString()))
                    .build();
            results.add(dto);
        }
        return results;
    }
}