package br.com.microservices.orchestrated.orderservice.repository;

import br.com.microservices.orchestrated.orderservice.document.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
