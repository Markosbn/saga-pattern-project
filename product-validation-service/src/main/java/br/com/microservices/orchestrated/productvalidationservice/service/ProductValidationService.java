package br.com.microservices.orchestrated.productvalidationservice.service;

import br.com.microservices.orchestrated.productvalidationservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.productvalidationservice.dto.EventDto;
import br.com.microservices.orchestrated.productvalidationservice.dto.HistoryDto;
import br.com.microservices.orchestrated.productvalidationservice.enums.ESagaStatus;
import br.com.microservices.orchestrated.productvalidationservice.model.Validation;
import br.com.microservices.orchestrated.productvalidationservice.producer.KafkaProducer;
import br.com.microservices.orchestrated.productvalidationservice.repository.ProductRepository;
import br.com.microservices.orchestrated.productvalidationservice.repository.ValidationRepository;
import br.com.microservices.orchestrated.productvalidationservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class ProductValidationService {

    private static final String CURRENT_SOURCE = "PRODUCT VALIDATION SERVICE";

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;
    private ProductRepository productRepository;
    private ValidationRepository validationRepository;

    public void validateExistProducts(EventDto event) {
        try {
            checkCurrentValidation(event);
            buildValidationObject(event, true);
            handleSucess(event);
        } catch (Exception e) {
            log.error("Error trying to validade prodcts: ", e);
            handleFail(event, e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void handleSucess(EventDto event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        event.addToHistory(buildHistoryDto(event, "Product validation successful!"));
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
        event.addToHistory(buildHistoryDto(event, "Product validation failed: ".concat(message)));
    }

    private void buildValidationObject(EventDto event, boolean success) {
        var validation = Validation.builder()
                .orderId(event.getOrderId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }

    private void checkCurrentValidation(EventDto event) {
        if (ObjectUtils.isEmpty(event.getPayload()) || ObjectUtils.isEmpty(event.getPayload().getProducts())) {
            throw new ValidationException("Product List is empty");
        }

        if (ObjectUtils.isEmpty(event.getPayload().getId()) || ObjectUtils.isEmpty(event.getPayload().getTransactionId())) {
            throw new ValidationException("Order Id and Transaction ID must be informed!");
        }

        if (validationRepository.findFirstByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId()).isPresent()) {
            throw new ValidationException("There's another transactionId for this validation!");
        }

        event.getPayload().getProducts().forEach(product -> {
            if (ObjectUtils.isEmpty(product.getProduct()) || ObjectUtils.isEmpty(product.getProduct().getCode())) {
                throw new ValidationException("Product must be informed!");
            }
            var codProduct = product.getProduct().getCode();

            if (!productRepository.existsByCode(codProduct)) {
                throw new ValidationException("Product code " + codProduct + " does not exist!");
            }
        });
    }

    public void rollBackEvent(EventDto event) {
        validationRepository.findFirstByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())
                .ifPresentOrElse(validation -> {
                    validation.setSuccess(false);
                    validationRepository.save(validation);
                }, () -> buildValidationObject(event, false));

        event.setStatus(ESagaStatus.FAIL);
        event.setSource(CURRENT_SOURCE);
        event.addToHistory(buildHistoryDto(event, "Rollback successful on product validation!"));
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }
}

