package br.com.microservices.orchestrated.orderservice.utils;

import br.com.microservices.orchestrated.orderservice.document.Event;
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

    public Event toEvent(String json) {
        try{
            return objectMapper.readValue(json, Event.class);
        } catch (Exception e) {
            return null;
        }
    }
}
