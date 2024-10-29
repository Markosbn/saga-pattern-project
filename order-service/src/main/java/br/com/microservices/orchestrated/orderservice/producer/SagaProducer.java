package br.com.microservices.orchestrated.orderservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.start-saga}")
    private String startSataTopic;

    public void sendEvent(String payload) {
        try {
            log.info("Sending event to topic {} with data {}", startSataTopic, payload);
            kafkaTemplate.send(startSataTopic, payload);
        } catch (Exception e) {
            log.error("Error trying send data to topic {} with data {}", startSataTopic, payload);
        }
    }
}
