package co.whitetree.redisperformance.service.template;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductCacheTemplate extends CacheTemplate<Long, Product> {
    private final ProductRepository productRepository;
    private final RMapReactive<Long, Product> map;

    public ProductCacheTemplate(ProductRepository productRepository, RedissonReactiveClient client) {
        this.productRepository = productRepository;
        this.map = client.getMap("product", new TypedJsonJacksonCodec(Long.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Long id) {
        return productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Long id) {
        return map.get(id);
    }

    @Override
    protected Mono<Product> updateSource(Long id, Product product) {
        return productRepository.findById(id)
                .doOnNext(p -> product.setId(id))
                .flatMap(p -> productRepository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Long id, Product product) {
        return map.fastPut(id, product).thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Long id) {
        return map.fastRemove(id).then();
    }
}
