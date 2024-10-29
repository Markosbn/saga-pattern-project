package br.com.microservices.orchestrated.orderservice.dto;

import br.com.microservices.orchestrated.orderservice.document.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductsDto {

    private Product product;
    private int quantity;
}
