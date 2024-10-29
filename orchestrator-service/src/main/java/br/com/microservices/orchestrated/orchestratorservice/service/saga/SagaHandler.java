package br.com.microservices.orchestrated.orchestratorservice.service.saga;

import br.com.microservices.orchestrated.orchestratorservice.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.enums.ESagaStatus;
import br.com.microservices.orchestrated.orchestratorservice.enums.ETopics;


public final class SagaHandler {

    public SagaHandler() {
    }

    public static final Object[][] SAGA_HANDLER = {
            {EEventSource.ORCHESTRATOR, ESagaStatus.SUCCESS, ETopics.PRODUCT_VALIDATION_SUCCESS},
            {EEventSource.ORCHESTRATOR, ESagaStatus.FAIL, ETopics.FINISH_FAIL},

            {EEventSource.PRODUCT_VALIDATION_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.PRODUCT_VALIDATION_FAIL},
            {EEventSource.PRODUCT_VALIDATION_SERVICE, ESagaStatus.FAIL, ETopics.FINISH_FAIL},
            {EEventSource.PRODUCT_VALIDATION_SERVICE, ESagaStatus.SUCCESS, ETopics.PAYMENT_SUCCESS},

            {EEventSource.PAYMENT_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.PAYMENT_FAIL},
            {EEventSource.PAYMENT_SERVICE, ESagaStatus.FAIL, ETopics.PRODUCT_VALIDATION_FAIL},
            {EEventSource.PAYMENT_SERVICE, ESagaStatus.SUCCESS, ETopics.INVENTORY_SUCCESS},

            {EEventSource.INVENTORY_SERVICE, ESagaStatus.ROLLBACK_PENDING, ETopics.INVENTORY_FAIL},
            {EEventSource.INVENTORY_SERVICE, ESagaStatus.FAIL, ETopics.PAYMENT_FAIL},
            {EEventSource.INVENTORY_SERVICE, ESagaStatus.SUCCESS, ETopics.FINISH_SUCCESS}
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;
    
}
