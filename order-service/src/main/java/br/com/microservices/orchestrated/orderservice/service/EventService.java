package br.com.microservices.orchestrated.orderservice.service;

import br.com.microservices.orchestrated.orderservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orderservice.document.Event;
import br.com.microservices.orchestrated.orderservice.dto.EventFilter;
import br.com.microservices.orchestrated.orderservice.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public void notifyEnding(Event event) {
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! TransactionId: {}", event.getOrderId(), event.getTransactionId());
    }

    public List<Event> findAll() {
        return eventRepository.findAllByOrderByCreatedAtDesc();
    }

    public Event findByFilter(EventFilter filter) {
        if (ObjectUtils.isEmpty(filter.getOrderId()) && ObjectUtils.isEmpty(filter.getTransactionId())) {
            throw  new ValidationException("OrderId or TransactionId cannot be empty");
        }

        return eventRepository.findFirstByOrderIdOrTransactionIdOrderByCreatedAtDesc(filter.getOrderId(), filter.getTransactionId())
                .orElseThrow(() -> new ValidationException(String.format("Event not found by orderId %s or transactionalId %s",  filter.getOrderId(), filter.getTransactionId())));
    }
}
