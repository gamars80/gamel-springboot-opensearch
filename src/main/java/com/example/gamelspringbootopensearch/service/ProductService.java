package com.example.gamelspringbootopensearch.service;

import com.example.gamelspringbootopensearch.config.RabbitMQConfig;
import com.example.gamelspringbootopensearch.dto.*;
import com.example.gamelspringbootopensearch.entity.*;
import com.example.gamelspringbootopensearch.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final RestHighLevelClient client;
    private static final String INDEX = "products";


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductPriceRepository priceRepository;
    private final ProductCategoryMappingRepository productCategoryMappingRepository;
    private final ProductHashtagRepository productHashtagRepository;
    private final ProductHashtagMappingRepository productHashtagMappingRepository;
    private final RabbitTemplate rabbitTemplate;

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
                    .id(Long.valueOf(hit.getId())) // hit.getId() 사용
                    .name(src.get("name").toString())
                    .description(src.get("description").toString())
                    .categoryId(Long.valueOf(src.get("categoryId").toString()))
                    .build();
            results.add(dto);
        }
        return results;
    }

    @Transactional
    public Product registerProduct(ProductRegistrationRequest request) {
        // 카테고리 검증 및 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 상품 엔티티 생성 (상품ID가 외부에서 제공되면 사용, 없으면 null로 두어 DB에서 생성)
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Product savedProduct = productRepository.save(product);

        // 가격 정보 저장
        ProductPrice price = ProductPrice.builder()
                .product(savedProduct)
                .price(request.getPrice())
                .build();
        priceRepository.save(price);


        ProductCategoryMapping productCategoryMapping = ProductCategoryMapping.builder()
                .product(savedProduct)
                .category(category)
                .build();

        productCategoryMappingRepository.save(productCategoryMapping);

        // 해시태그 매핑 처리
        if (request.getHashtagIds() != null) {
            for (Long hashtagId : request.getHashtagIds()) {
                ProductHashtag hashtag = productHashtagRepository.findById(hashtagId)
                        .orElseThrow(() -> new RuntimeException("Hashtag not found with id: " + hashtagId));

                ProductHashtagMapping productHashtagMapping = ProductHashtagMapping.builder()
                        .product(savedProduct)
                        .hashtag(hashtag)
                        .build();
                productHashtagMappingRepository.save(productHashtagMapping);
            }
        }

        // OpenSearch 인덱싱 요청 메시지 발행
        ProductRegistrationMessage message = ProductRegistrationMessage.builder()
                .productId(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .categoryId(category.getId())
                .price(request.getPrice())
                .hashtagIds(request.getHashtagIds())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        return savedProduct;
    }


    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequest updateRequest) {
        // MySQL에서 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // 상품 정보 업데이트
        product.updateProduct(updateRequest.getName());

        ProductPrice price = priceRepository.findByProduct(product);
        if( price == null) {
            new RuntimeException("Product not found with id: " + productId);
        }

        price.updateProductPrice(updateRequest.getPrice());

        // OpenSearch 업데이트를 위한 메시지 생성
        ProductUpdateMessage message = new ProductUpdateMessage();
        message.setProductId(product.getId());
        message.setName(product.getName());
        message.setPrice(price.getPrice());

        // RabbitMQ로 메시지 전송
        // 재처리 및 DLQ 처리는 RabbitMQ의 컨슈머와 별도 설정(예: Spring Retry, DLQ 큐)에서 수행됩니다.
        rabbitTemplate.convertAndSend(RabbitMQConfig.UPDATE_EXCHANGE,
                RabbitMQConfig.UPDATE_ROUTING_KEY, message);

    }
}