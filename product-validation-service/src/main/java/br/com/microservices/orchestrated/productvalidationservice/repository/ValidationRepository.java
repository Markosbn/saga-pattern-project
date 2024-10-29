package br.com.microservices.orchestrated.productvalidationservice.repository;

import br.com.microservices.orchestrated.productvalidationservice.model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {

    Optional<Validation> findFirstByOrderIdAndTransactionId(String orderId, String transactionId);

}
