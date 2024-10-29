package br.com.microservices.orchestrated.orchestratorservice.dto;

import br.com.microservices.orchestrated.orchestratorservice.enums.EEventSource;
import br.com.microservices.orchestrated.orchestratorservice.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HistoryDto {

    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;

}
