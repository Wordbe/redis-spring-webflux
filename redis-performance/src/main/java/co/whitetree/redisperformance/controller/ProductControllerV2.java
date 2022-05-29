package co.whitetree.redisperformance.controller;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.service.ProductServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProductControllerV2 {

    private final ProductServiceV2 productServiceV2;

    @GetMapping("/product/v2/{id}")
    Mono<Product> getProduct(@PathVariable Long id) {
        return productServiceV2.getProduct(id);
    }

    @PutMapping("/product/v2/{id}")
    Mono<Product> updateProduct(@PathVariable Long id, @RequestBody Mono<Product> productMono) {
        return productServiceV2.updateProduct(id, productMono);
    }

    @DeleteMapping("/product/v2/{id}")
    Mono<Void> deleteProduct(@PathVariable Long id) {
        return productServiceV2.deleteProduct(id);
    }
}
