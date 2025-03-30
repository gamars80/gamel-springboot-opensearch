package com.example.gamelspringbootopensearch.listner;

import com.example.gamelspringbootopensearch.config.RabbitMQConfig;
import com.example.gamelspringbootopensearch.dto.ProductUpdateMessage;
import com.example.gamelspringbootopensearch.service.OpenSearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductUpdateListener {

    private final OpenSearchService openSearchService;

    public ProductUpdateListener(OpenSearchService openSearchService) {
        this.openSearchService = openSearchService;
    }

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_UPDATE_QUEUE)
    public void handleProductUpdate(ProductUpdateMessage message) {
        openSearchService.updateProduct(message);
    }
}