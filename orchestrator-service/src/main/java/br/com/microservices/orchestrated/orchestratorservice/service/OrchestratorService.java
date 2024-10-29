package br.com.microservices.orchestrated.orchestratorservice.service;

import br.com.microservices.orchestrated.orchestratorservice.dto.EventDto;
import br.com.microservices.orchestrated.orchestratorservice.dto.HistoryDto;
import br.com.microservices.orchestrated.orchestratorservice.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.enums.ESagaStatus;
import br.com.microservices.orchestrated.orchestratorservice.enums.ETopics;
import br.com.microservices.orchestrated.orchestratorservice.producer.SagaOrchestratorProducer;
import br.com.microservices.orchestrated.orchestratorservice.service.saga.SagaExecutionController;
import br.com.microservices.orchestrated.orchestratorservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestratorService {

    private final SagaExecutionController sagaController;
    private final SagaOrchestratorProducer producer;
    private final JsonUtil jsonUtil;

    public void startSaga(EventDto event) {
        event.setSource(EEventSource.ORCHESTRATOR);
        event.setStatus(ESagaStatus.SUCCESS);
        ETopics topic = sagaController.getNextTopic(event);
        log.info("### SAGA STARTED");
        event.addToHistory(buildHistoryDto(event, "Saga started."));
        sendSagaToKafka(event, topic);
    }

    public void finishSagaSuccess(EventDto event) {
        event.setSource(EEventSource.ORCHESTRATOR);
        event.setStatus(ESagaStatus.SUCCESS);
        log.info("### SAGA FINISHED SUCCESSFULLY {}", event.getId());
        event.addToHistory(buildHistoryDto(event, "Saga finished successfully."));
        notifyEndingSaga(event);
    }

    public void finishSagaFail(EventDto event) {
        event.setSource(EEventSource.ORCHESTRATOR);
        event.setStatus(ESagaStatus.FAIL);
        log.info("### SAGA FINISHED WITH ERRORS {}", event.getId());
        event.addToHistory(buildHistoryDto(event, "Saga finished with erros."));
        notifyEndingSaga(event);
    }

    public void continueSaga(EventDto event) {
        ETopics topic = sagaController.getNextTopic(event);
        log.info("### SAGA CONTINUING {}", event.getId());
        event.addToHistory(buildHistoryDto(event, "Saga continuing."));
        sendSagaToKafka(event, topic);
    }

    private void sendSagaToKafka(EventDto event, ETopics topic) {
        producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    private void notifyEndingSaga(EventDto event) {
        sendSagaToKafka(event, ETopics.NOTIFY_ENDING);
    }

    private HistoryDto buildHistoryDto(EventDto event, String message) {
        return HistoryDto.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
