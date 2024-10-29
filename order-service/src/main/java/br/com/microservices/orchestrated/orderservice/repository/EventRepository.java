package br.com.microservices.orchestrated.orderservice.repository;

import br.com.microservices.orchestrated.orderservice.document.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findAllByOrderByCreatedAtDesc();

    Optional<Event> findFirstByOrderIdOrTransactionIdOrderByCreatedAtDesc(String orderId, String transactionId);
}
