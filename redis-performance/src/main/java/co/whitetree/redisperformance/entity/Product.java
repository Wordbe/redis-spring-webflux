package co.whitetree.redisperformance.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table // r2dbc
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
