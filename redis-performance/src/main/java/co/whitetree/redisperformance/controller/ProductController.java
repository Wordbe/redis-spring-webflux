package co.whitetree.redisperformance.controller;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/{id}")
    Mono<Product> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PutMapping("/product/{id}")
    Mono<Product> updateProduct(@PathVariable Long id, @RequestBody Mono<Product> productMono) {
        return productService.updateProduct(id, productMono);
    }
}
