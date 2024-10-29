package br.com.microservices.orchestrated.orderservice.controller;

import br.com.microservices.orchestrated.orderservice.document.Event;
import br.com.microservices.orchestrated.orderservice.document.Order;
import br.com.microservices.orchestrated.orderservice.dto.EventFilter;
import br.com.microservices.orchestrated.orderservice.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.service.EventService;
import br.com.microservices.orchestrated.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private EventService eventService;

    @GetMapping
    public Event findByFilters(EventFilter filter) {
        return eventService.findByFilter(filter);
    }

    @GetMapping("/all")
    public List<Event> findAll() {
        return eventService.findAll();
    }
}
