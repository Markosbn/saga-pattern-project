package br.com.microservices.orchestrated.paymentservice.repository;

import br.com.microservices.orchestrated.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findFirstByOrderIdAndTransactionId(String orderId, String transactionId);
    Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);

}
