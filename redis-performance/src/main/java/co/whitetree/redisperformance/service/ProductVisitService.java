package co.whitetree.redisperformance.service;

import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductVisitService {

    private final RedissonReactiveClient redissonReactiveClient;
    private final Sinks.Many<Long> sink;

    public ProductVisitService(RedissonReactiveClient redissonReactiveClient) {
        this.redissonReactiveClient = redissonReactiveClient;
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    private void init() {
        sink.asFlux()
                .buffer(Duration.ofSeconds(3)) // list (1, 2, 1, 1, 3, 5, 1, ...)
                .map(l -> l.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))) // map 1:4, 2:1, 3:1, 5:1, ...
                .flatMap(this::updateBatch)
                .subscribe();
    }

    public void addVisit(Long productId) {
        sink.tryEmitNext(productId);
    }

    private Mono<Void> updateBatch(Map<Long, Long> map) {
        RBatchReactive batch = redissonReactiveClient.createBatch(BatchOptions.defaults());
        String yyyymMdd = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Long> scoredSortedSet = batch.getScoredSortedSet("product:visit:" + yyyymMdd, IntegerCodec.INSTANCE);

        return Flux.fromIterable(map.entrySet())
                .map(e -> scoredSortedSet.addScore(e.getKey(), e.getValue()))
                .then(batch.execute())
                .then();
    }
}
