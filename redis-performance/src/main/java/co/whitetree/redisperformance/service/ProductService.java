package co.whitetree.redisperformance.service;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Mono<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> updateProduct(Long id, Mono<Product> productMono) {
        return productRepository.findById(id)
                .flatMap(product -> productMono.doOnNext(pm -> pm.setId(id)))
                .flatMap(productRepository::save);
    }
}
