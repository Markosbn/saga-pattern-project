package br.com.microservices.orchestrated.inventoryservice.consumer;

import br.com.microservices.orchestrated.inventoryservice.service.InventoryService;
import br.com.microservices.orchestrated.inventoryservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class InventoryConsumer {

    private final InventoryService inventoryService;
    private final JsonUtil jsonUtil;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.inventory-success}")
    public void consumeSuccessEvent(String payload) {
        log.info("Receiving event {} from topic inventory-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        inventoryService.updateInventory(event);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.inventory-fail}")
    public void consumeFailEvent(String payload) {
        log.info("Receiving event {} from topic inventory-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        inventoryService.rollBackEvent(event);
    }

}
