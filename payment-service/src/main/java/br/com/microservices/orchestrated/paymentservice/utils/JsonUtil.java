package br.com.microservices.orchestrated.paymentservice.utils;

import br.com.microservices.orchestrated.paymentservice.dto.EventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String toJson(Object obj) {
        try{
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "";
        }
    }

    public EventDto toEvent(String json) {
        try{
            return objectMapper.readValue(json, EventDto.class);
        } catch (Exception e) {
            return null;
        }
    }
}
