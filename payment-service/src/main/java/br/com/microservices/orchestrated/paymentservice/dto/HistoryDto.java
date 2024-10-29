package br.com.microservices.orchestrated.paymentservice.dto;


import br.com.microservices.orchestrated.paymentservice.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HistoryDto {

    private String source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;

}
