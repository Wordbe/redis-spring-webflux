package co.whitetree.redisperformance.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessMetricsService {

    private final RedissonReactiveClient redissonReactiveClient;

    public Mono<Map<Long, Double>> top3Products() {
        String yyyymMdd = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Long> scoredSortedSet = redissonReactiveClient.getScoredSortedSet("product:visit:" + yyyymMdd, IntegerCodec.INSTANCE);
        return scoredSortedSet.entryRangeReversed(0, 2) // list of scored entry
                .map(list -> list.stream().collect(
                        Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (a, b) -> a,
                                LinkedHashMap::new))); // for sorting
    }

    /*
        {
            productId1: count1,
            productId2: count2,
            productId3: count3
        }
     */
}
