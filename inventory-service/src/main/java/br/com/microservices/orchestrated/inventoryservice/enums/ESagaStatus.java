package br.com.microservices.orchestrated.inventoryservice.enums;

public enum ESagaStatus {

    SUCCESS,
    ROLLBACK_PENDING,
    FAIL;
}
