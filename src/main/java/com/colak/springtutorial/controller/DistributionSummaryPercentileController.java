package com.colak.springtutorial.controller;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping(path = "/percentile")
@RequiredArgsConstructor
public class DistributionSummaryPercentileController {



    private DistributionSummary distributionSummary;

    @Autowired
    public void setRegistry(MeterRegistry registry) {
        distributionSummary = createDistributionSummary(registry);
    }

    // http://localhost:8080/percentile/read
    // http://localhost:8080/actuator/prometheus

    @GetMapping("/read")
    public String read() throws InterruptedException {
        // Imitating call latency

        // Note: Micrometer’s DistributionSummary uses the long data type to keep track of the bucket boundaries.
        // If your metrics contain values that are less than one, you will need to multiply these values by a scale number
        // to avoid losing precision when your values are converted to the long datatype.
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long millis = 10 + random.nextLong(50);
        Thread.sleep(millis);

        distributionSummary.record(millis);
        return String.valueOf(millis);
    }

    // If you specify percentiles (but DO NOT enable SLO/SLA or publishPercentileHistogram), it will produce a Prometheus
    // summary metric, returning the count, total, maximum, and the specified quantile numbers of the metric

    // DistributionSummaryPercentile{application="spring-boot-micrometer-tutorial",region="us-east",quantile="0.25"} 10.0
    // DistributionSummaryPercentile{application="spring-boot-micrometer-tutorial",region="us-east",quantile="0.5"} 23.5
    // DistributionSummaryPercentile{application="spring-boot-micrometer-tutorial",region="us-east",quantile="0.75"} 51.5
    // DistributionSummaryPercentile{application="spring-boot-micrometer-tutorial",region="us-east",quantile="0.95"} 51.5
    // DistributionSummaryPercentile_count{application="spring-boot-micrometer-tutorial",region="us-east"} 3
    // DistributionSummaryPercentile_sum{application="spring-boot-micrometer-tutorial",region="us-east"} 84.0
    // DistributionSummaryPercentile_max{application="spring-boot-micrometer-tutorial",region="us-east"} 51.0
    private DistributionSummary createDistributionSummary(MeterRegistry meterRegistry) {
        // Count how many times this API has been called
        return DistributionSummary.builder("DistributionSummaryPercentile")
                .description("A custom distribution summary for percentile")
                .maximumExpectedValue(100.0)
                .publishPercentiles(0.25, 0.5, 0.75, 0.95)
                .tag("region", "us-east")
                .register(meterRegistry);
    }
}
