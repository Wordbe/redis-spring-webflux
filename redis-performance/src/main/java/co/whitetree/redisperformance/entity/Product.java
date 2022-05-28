package co.whitetree.redisperformance.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class Product {

    @Id
    private Long id;
    private String description;
    private BigDecimal price;

    public Product(String description, BigDecimal price) {
        this.description = description;
        this.price = price;
    }

    public static Product of(String description, BigDecimal price) {
        return new Product(description, price);
    }
}
