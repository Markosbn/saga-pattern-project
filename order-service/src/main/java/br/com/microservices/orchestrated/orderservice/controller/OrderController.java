package br.com.microservices.orchestrated.orderservice.controller;

import br.com.microservices.orchestrated.orderservice.document.Order;
import br.com.microservices.orchestrated.orderservice.dto.OrderRequest;
import br.com.microservices.orchestrated.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public Order createOrder(@RequestBody OrderRequest order) {
        return orderService.createOrder(order);
    }
}
