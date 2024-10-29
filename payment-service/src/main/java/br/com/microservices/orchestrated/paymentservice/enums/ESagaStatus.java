package br.com.microservices.orchestrated.paymentservice.enums;

public enum ESagaStatus {

    SUCCESS,
    ROLLBACK_PENDING,
    FAIL;
}
