package br.com.microservices.orchestrated.productvalidationservice.consumer;

import br.com.microservices.orchestrated.productvalidationservice.service.ProductValidationService;
import br.com.microservices.orchestrated.productvalidationservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class ProductValidationConsumer {

    private final JsonUtil jsonUtil;
    private final ProductValidationService productValidationService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.product-validation-success}")
    public void consumeSuccessEvent(String payload) {
        log.info("Receiving event {} from topic product-validation-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        productValidationService.validateExistProducts(event);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.product-validation-fail}")
    public void consumeFailEvent(String payload) {
        log.info("Receiving event {} from topic product-validation-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        productValidationService.rollBackEvent(event);
    }

}
