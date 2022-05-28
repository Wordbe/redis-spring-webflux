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
}
