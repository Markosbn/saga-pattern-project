package br.com.microservices.orchestrated.paymentservice.consumer;

import br.com.microservices.orchestrated.paymentservice.service.PaymentService;
import br.com.microservices.orchestrated.paymentservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentConsumer {

    private final JsonUtil jsonUtil;
    private final PaymentService paymentService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.payment-success}")
    public void consumeSuccessEvent(String payload) {
        log.info("Receiving event {} from topic payment-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.doPayment(event);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.topic.payment-fail}")
    public void consumeFailEvent(String payload) {
        log.info("Receiving event {} from topic payment-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.doRefund(event);
    }

}
