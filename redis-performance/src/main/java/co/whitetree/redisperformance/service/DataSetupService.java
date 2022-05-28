package co.whitetree.redisperformance.service;

import co.whitetree.redisperformance.entity.Product;
import co.whitetree.redisperformance.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class DataSetupService implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final Resource resource;

    public DataSetupService(ProductRepository productRepository,
                            R2dbcEntityTemplate r2dbcEntityTemplate, // why no bean found?
                            @Value("classpath:schema.sql") Resource resource) {
        this.productRepository = productRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.resource = resource;
    }

    @Override
    public void run(String... args) throws Exception {
        String ddl = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        log.info(ddl);

        Mono<Void> saveProducts = Flux.range(1, 1000)
                .map(i -> Product.of("product " + i, BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(1, 100))))
                .collectList()
                .flatMapMany(productRepository::saveAll)
                .then();

        // took 1m 30s
        r2dbcEntityTemplate.getDatabaseClient()
                .sql(ddl)
                .then()
                .then(saveProducts)
                .doFinally(signalType -> log.info("data setup done: " + signalType))
                .subscribe();
    }
}
