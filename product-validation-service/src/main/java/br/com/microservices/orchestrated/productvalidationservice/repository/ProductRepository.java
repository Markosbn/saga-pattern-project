package br.com.microservices.orchestrated.productvalidationservice.repository;

import br.com.microservices.orchestrated.productvalidationservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByCode(String name);
}
