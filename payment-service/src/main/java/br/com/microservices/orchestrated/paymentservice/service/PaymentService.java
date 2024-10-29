package br.com.microservices.orchestrated.paymentservice.service;

import br.com.microservices.orchestrated.paymentservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.paymentservice.dto.EventDto;
import br.com.microservices.orchestrated.paymentservice.dto.HistoryDto;
import br.com.microservices.orchestrated.paymentservice.dto.OrderProductsDto;
import br.com.microservices.orchestrated.paymentservice.enums.EPaymentStatus;
import br.com.microservices.orchestrated.paymentservice.enums.ESagaStatus;
import br.com.microservices.orchestrated.paymentservice.model.Payment;
import br.com.microservices.orchestrated.paymentservice.producer.KafkaProducer;
import br.com.microservices.orchestrated.paymentservice.repository.PaymentRepository;
import br.com.microservices.orchestrated.paymentservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private static final String CURRENT_SOURCE = "PAYMENT_SERVICE";

    private final JsonUtil jsonUtil;
    private final PaymentRepository paymentRepository;
    private final KafkaProducer kafkaProducer;

    public void doPayment(EventDto event) {
        try{
            checkCurrentValidation(event);
            createPendingPayment(event);
            Payment payment = this.findPaymentByOrderIdAndTransactionId(event);
            validateAmount(payment.getTotalAmount());
            handleSuccess(payment);
            handleSucess(event);
        } catch (Exception e) {
            log.error("Error trying to process payment: ", e.getMessage());
            handleFail(event, e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleSucess(EventDto event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        event.addToHistory(buildHistoryDto(event, "Payment successful!"));
    }

    private HistoryDto buildHistoryDto(EventDto event, String message) {
        return HistoryDto.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void handleFail(EventDto event, String message) {
        event.setStatus(ESagaStatus.ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        event.addToHistory(buildHistoryDto(event, "Payment failed: ".concat(message)));
    }

    public void doRefund(EventDto event) {
        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        try {
            var payment = this.findPaymentByOrderIdAndTransactionId(event);
            payment.setStatus(EPaymentStatus.REFUND);
            save(payment);
            buildHistoryDto(event, "Roll back payment successful!");
        } catch (Exception e) {
            buildHistoryDto(event, "Roll back not executed for payment: ".concat(e.getMessage()));
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleSuccess(Payment payment) {
        payment.setStatus(EPaymentStatus.SUCCESS);
        save(payment);
    }

    private void validateAmount(Double totalAmount) {
        if (totalAmount <= 0) {
            throw new ValidationException("Total amount must be greater than zero");
        }
    }

    private void createPendingPayment(EventDto event) {
        Double amount = calculateAmount(event);
        Integer totalItems = calculateItens(event);
        var payment = Payment.builder()
                .orderId(event.getOrderId())
                .transactionId(event.getTransactionId())
                .totalAmount(amount)
                .totalItems(totalItems)
                .build();

        event.getPayload().setTotalAmount(amount);
        event.getPayload().setTotalItems(totalItems);

        save(payment);
    }

    private Integer calculateItens(EventDto event) {
        return event.getPayload()
                .getProducts()
                .stream()
                .map(OrderProductsDto::getQuantity)
                .reduce(0, Integer::sum);
    }

    private Double calculateAmount(EventDto event) {
        return event.getPayload()
                .getProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().getUnitValue())
                .reduce(0.0, Double::sum);
    }

    private void save(Payment payment) {
        paymentRepository.save(payment);
    }

    private void checkCurrentValidation(EventDto event) {
        if (paymentRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's another transactionId and OrderId for this validation.");
        }
    }

    private Payment findPaymentByOrderIdAndTransactionId(EventDto event) {
        return paymentRepository.findFirstByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
                .orElseThrow(() -> new ValidationException("There is no payment with this orderId and TransactionId."));
    }
}
