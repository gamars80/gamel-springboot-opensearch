package com.example.gamelspringbootopensearch.listner;


import com.example.gamelspringbootopensearch.config.RabbitMQConfig;
import com.example.gamelspringbootopensearch.dto.ProductRegistrationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DLQListener {

    @RabbitListener(queues = RabbitMQConfig.PRODUCT_REGISTRATION_DLQ)
    public void handleDLQ(ProductRegistrationMessage message) {
        log.error("Message moved to DLQ: {}", message);
        // 추가적인 후처리(예: 관리자 알림 등)를 구현할 수 있습니다.
    }
}