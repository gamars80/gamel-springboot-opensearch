package com.example.gamelspringbootopensearch.listner;


import com.example.gamelspringbootopensearch.config.RabbitMQConfig;
import com.example.gamelspringbootopensearch.dto.ProductRegistrationMessage;
import com.example.gamelspringbootopensearch.service.OpenSearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRegistrationListener {

    private final OpenSearchService openSearchService;
    private final Logger logger = LoggerFactory.getLogger(ProductRegistrationListener.class);

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_REGISTRATION_QUEUE)
    public void handleProductRegistration(ProductRegistrationMessage message) {
        try {
            openSearchService.indexProduct(message);
        } catch (Exception e) {
            logger.error("OpenSearch indexing failed for productId {}: {}", message.getProductId(), e.getMessage());
            // 예외 발생 시 재시도 (최대 3회) 후 DLQ로 이동
            throw e;
        }
    }
}