package br.com.microservices.orchestrated.orderservice.service;

import br.com.microservices.orchestrated.orderservice.document.Event;
import br.com.microservices.orchestrated.orderservice.document.Order;
import br.com.microservices.orchestrated.orderservice.document.OrderProducts;
import br.com.microservices.orchestrated.orderservice.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.producer.SagaProducer;
import br.com.microservices.orchestrated.orderservice.repository.OrderRepository;
import br.com.microservices.orchestrated.orderservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private final EventService eventService;
    private final JsonUtil jsonUtil;
    private final OrderRepository orderRepository;
    private final SagaProducer sagaProducer;

    private static final String TRANSACTION_ID_PATTERN = "%s_%s";

    public Order createOrder(OrderRequest request) {
        Order order = Order.builder()
                .products(request.getProducts().stream().map(OrderProducts::new).toList())
                .createdAt(LocalDateTime.now())
                .transactionId(String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID()))
                .build();
        order = orderRepository.save(order);
        sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));
        return order;
    }

    public Event createPayload(Order order) {
        Event event = Event.builder()
                .orderId(order.getId())
                .transactionId(order.getTransactionId())
                .payload(order)
                .createdAt(LocalDateTime.now())
                .build();

        return eventService.save(event);
    }
}
