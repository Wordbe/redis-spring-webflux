package co.whitetree.redisperformance.controller;

import co.whitetree.redisperformance.service.BusinessMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BusinessMetricsController {
    private final BusinessMetricsService businessMetricsService;

    @GetMapping(value = "product/metrics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Map<Long, Double>> getMetrics() {
        return businessMetricsService.top3Products()
                .repeatWhen(longFlux -> Flux.interval(Duration.ofSeconds(3)));
    }
}
