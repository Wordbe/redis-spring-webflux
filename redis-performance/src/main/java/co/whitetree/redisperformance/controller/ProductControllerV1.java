package co.whitetree.redisperformance.controller;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.service.ProductServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProductControllerV1 {

    private final ProductServiceV1 productServiceV1;

    @GetMapping("/product/v1/{id}")
    Mono<Product> getProduct(@PathVariable Long id) {
        return productServiceV1.getProduct(id);
    }

    @PutMapping("/product/v1/{id}")
    Mono<Product> updateProduct(@PathVariable Long id, @RequestBody Mono<Product> productMono) {
        return productServiceV1.updateProduct(id, productMono);
    }
}
