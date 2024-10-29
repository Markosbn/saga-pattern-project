package br.com.microservices.orchestrated.orchestratorservice.service.saga;

import br.com.microservices.orchestrated.orchestratorservice.config.exception.ValidationException;
import br.com.microservices.orchestrated.orchestratorservice.dto.EventDto;
import br.com.microservices.orchestrated.orchestratorservice.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.enums.ESagaStatus;
import br.com.microservices.orchestrated.orchestratorservice.enums.ETopics;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    public ETopics getNextTopic(EventDto event) {
        if (ObjectUtils.isEmpty(event) || ObjectUtils.isEmpty(event.getSource()) || ObjectUtils.isEmpty(event.getStatus())) {
            throw new ValidationException("Source or status must be informed");
        }
        ETopics topic = this.findTopicsBySourceAndStatus(event.getSource(), event.getStatus());

        var sagaLogId = String.format("ORDER ID: %S | TRANSACTION ID: %S | EVENT ID: %S",event.getPayload().getId(), event.getTransactionId(), event.getId());
        log.info("### CURRENT SAGA: {} | {} | NEXT TOPIC: {} | {}", event.getSource(), event.getStatus(), topic, sagaLogId);

        return topic;
    }

    private ETopics findTopicsBySourceAndStatus(EEventSource source, ESagaStatus status) {
        return Arrays.stream(SagaHandler.SAGA_HANDLER)
                .filter(row -> this.isSourceAndStatusValido(source, status, row))
                .map(i -> (ETopics) (i[SagaHandler.TOPIC_INDEX]))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Topic not found"));
    }

    private boolean isSourceAndStatusValido(EEventSource source, ESagaStatus status, Object[] row) {
        var matrizSource =   row[SagaHandler.EVENT_SOURCE_INDEX];
        var matrizStatus =   row[SagaHandler.SAGA_STATUS_INDEX];
        return source.equals(matrizSource) && status.equals(matrizStatus);
    }



}
