package br.com.microservices.orchestrated.orderservice.document;

import br.com.microservices.orchestrated.orderservice.dto.OrderProductsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {

    private Product product;
    private int quantity;

    public OrderProducts(OrderProductsDto dto) {
        this.product = dto.getProduct();
        this.quantity = dto.getQuantity();
    }
}
