package br.com.microservices.orchestrated.paymentservice.dto;


import br.com.microservices.orchestrated.paymentservice.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventDto {

    private String id;
    private String transactionId;
    private String orderId;
    private OrderDto payload;
    private String source;
    private ESagaStatus status;
    private List<HistoryDto> eventHistory;

    public void addToHistory(HistoryDto history) {
        if (eventHistory == null) {
            eventHistory = new ArrayList<>();
        }
        eventHistory.add(history);
    }
}
