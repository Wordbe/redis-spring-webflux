package co.whitetree.redisperformance.service;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.repository.ProductRepository;
import co.whitetree.redisperformance.service.template.CacheTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceV2 {

    private final CacheTemplate<Long, Product> cacheTemplate;
    private final ProductVisitService productVisitService;

    public Mono<Product> getProduct(Long id) {
        return cacheTemplate.get(id)
                .doFirst(() -> productVisitService.addVisit(id));
    }

    public Mono<Product> updateProduct(Long id, Mono<Product> productMono) {
        return productMono.flatMap(product -> cacheTemplate.update(id, product));
    }

    public Mono<Void> deleteProduct(Long id) {
        return cacheTemplate.delete(id);
    }
}
