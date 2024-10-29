package br.com.microservices.orchestrated.orderservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class History {

    private String source;
    private String status;
    private String message;
    private LocalDateTime createdAt;

}