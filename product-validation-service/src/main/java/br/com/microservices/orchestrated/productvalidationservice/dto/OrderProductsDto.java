package br.com.microservices.orchestrated.productvalidationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductsDto {

    private ProductDto product;
    private int quantity;
}
