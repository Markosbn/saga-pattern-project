package br.com.microservices.orchestrated.inventoryservice.service;

import br.com.microservices.orchestrated.inventoryservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.inventoryservice.dto.EventDto;
import br.com.microservices.orchestrated.inventoryservice.dto.HistoryDto;
import br.com.microservices.orchestrated.inventoryservice.dto.OrderDto;
import br.com.microservices.orchestrated.inventoryservice.dto.OrderProductsDto;
import br.com.microservices.orchestrated.inventoryservice.enums.ESagaStatus;
import br.com.microservices.orchestrated.inventoryservice.model.Inventory;
import br.com.microservices.orchestrated.inventoryservice.model.OrderInventory;
import br.com.microservices.orchestrated.inventoryservice.producer.KafkaProducer;
import br.com.microservices.orchestrated.inventoryservice.repository.InventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.repository.OrderInventoryRepository;
import br.com.microservices.orchestrated.inventoryservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class InventoryService {

    private static final String CURRENT_SOURCE = "INVENTORY_SERVICE";

    private final JsonUtil jsonUtil;
    private final InventoryRepository inventoryRepository;
    private final KafkaProducer kafkaProducer;
    private final OrderInventoryRepository orderInventoryRepository;

    public void updateInventory(EventDto eventDto) {
        try {
            checkCurrentValidation(eventDto);
            createOrderInventory(eventDto);
            updateInventory(eventDto.getPayload());
            handleSucess(eventDto);
        } catch (Exception e) {
            log.error("error updating inventory", e.getMessage());
            handleFail(eventDto, e.getMessage());
        }


    }

    private void updateInventory(OrderDto payload) {
        payload.getProducts().forEach(product -> {
            Inventory inventory = findInventoryByProductCode(product.getProduct().getCode());
            if (!isInventoryAvailable(inventory.getQuantity(), product.getQuantity())) {
                throw new ValidationException(String.format("Product %s is not available", product.getProduct().getCode()));
            }
            inventory.setQuantity(inventory.getQuantity() - product.getQuantity());
            inventoryRepository.save(inventory);
        });
    }

    private boolean isInventoryAvailable(Integer inventory, int quantity) {
        return inventory < quantity;
    }

    private void handleSucess(EventDto event) {
        event.setStatus(ESagaStatus.SUCCESS);
        event.setSource(CURRENT_SOURCE);
        event.addToHistory(buildHistoryDto(event, "Inventory update successful!"));
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
        event.addToHistory(buildHistoryDto(event, "Inventory update failed: ".concat(message)));
    }

    private void rollBackInventory(EventDto eventDto) {
        orderInventoryRepository.findByOrderIdAndTransactionId(eventDto.getOrderId(), eventDto.getTransactionId())
                .forEach(orderInventory -> {
                    Inventory inventory = orderInventory.getInventory();
                    inventory.setQuantity(inventory.getQuantity() + orderInventory.getOrderQuantity());
                    inventoryRepository.save(inventory);
                    log.info("Returned inventory to the previous value");
                });
    }

    public void rollBackEvent(EventDto eventDto) {
        eventDto.setStatus(ESagaStatus.FAIL);
        eventDto.setSource(CURRENT_SOURCE);
        try {
            rollBackInventory(eventDto);
            buildHistoryDto(eventDto, "roll back executed for inventory!");
        } catch (Exception e) {
            buildHistoryDto(eventDto, "roll back failed!".concat(e.getMessage()));
        }
    }

    private void checkCurrentValidation(EventDto event) {
        if (orderInventoryRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())) {
            throw new ValidationException("There's already another transaction with this validation");
        }
    }

    private void createOrderInventory(EventDto eventDto) {
        eventDto.getPayload()
                .getProducts()
                .forEach(product -> {
                    var inventory = this.findInventoryByProductCode(product.getProduct().getCode());
                    OrderInventory orderInventory = this.createOrderInventory(eventDto, product, inventory);
                    orderInventoryRepository.save(orderInventory);
                });

    }

    private OrderInventory createOrderInventory(EventDto eventDto, OrderProductsDto product, Inventory inventory) {
        return OrderInventory
                .builder()
                .inventory(inventory)
                .orderId(eventDto.getOrderId())
                .transactionId(eventDto.getTransactionId())
                .oldQuantity(inventory.getQuantity())
                .orderQuantity(product.getQuantity())
                .newQuantity(inventory.getQuantity() - product.getQuantity())
                .build();
    }

    private Inventory findInventoryByProductCode(String productCode) {
        return inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ValidationException("There is no inventory with this product code"));
    }
}
