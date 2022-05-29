package co.whitetree.redisperformance.service.template;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.repository.ProductRepository;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Long, Product> {
    private final ProductRepository productRepository;
    private final RLocalCachedMap<Long, Product> localCachedMap;

    public ProductLocalCacheTemplate(ProductRepository productRepository, RedissonClient client) {
        this.productRepository = productRepository;

        LocalCachedMapOptions<Long, Product> mapOptions = LocalCachedMapOptions.<Long, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);
        this.localCachedMap = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Long.class, Product.class), mapOptions);
    }

    @Override
    protected Mono<Product> getFromSource(Long id) {
        return productRepository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Long id) {
        return Mono.justOrEmpty(localCachedMap.get(id));
    }

    @Override
    protected Mono<Product> updateSource(Long id, Product product) {
        return productRepository.findById(id)
                .doOnNext(p -> product.setId(id))
                .flatMap(p -> productRepository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Long id, Product product) {
        return Mono.create(sink ->
                localCachedMap.fastPutAsync(id, product)
                        .thenAccept(aBoolean -> sink.success(product))
                        .exceptionally(e -> {
                            sink.error(e);
                            return null;
                        }));
    }

    @Override
    protected Mono<Void> deleteFromSource(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Long id) {
        return Mono.create(sink ->
                localCachedMap.fastRemoveAsync(id)
                        .thenAccept(aBoolean -> sink.success())
                        .exceptionally(e -> {
                            sink.error(e);
                            return null;
                        }));
    }
}
