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
@RequestMapping(path = "/histogram")
@RequiredArgsConstructor
public class DistributionSummaryHistogramController {

    private DistributionSummary distributionSummary;

    @Autowired
    public void setRegistry(MeterRegistry registry) {
        distributionSummary = createDistributionSummary(registry);
    }

    // http://localhost:8080/histogram/read
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

    // If you specify SLO or enable publishPercentileHistogram in the DistributionSummary class, it will produce a Prometheus histogram metric,
    // returning the count, total, maximum, and the counts in each histogram bucket.

    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="1.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="2.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="3.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="4.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="5.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="6.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="7.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="8.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="9.0"} 0
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="10.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="11.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="12.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="13.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="14.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="16.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="21.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="26.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="31.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="36.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="41.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="46.0"} 1
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="51.0"} 2
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="56.0"} 2
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="64.0"} 2
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="85.0"} 2
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="100.0"} 2
    // DistributionSummaryHistogram_bucket{application="spring-boot-micrometer-tutorial",region="us-east",le="+Inf"} 2
    // DistributionSummaryHistogram_count{application="spring-boot-micrometer-tutorial",region="us-east"} 2
    // DistributionSummaryHistogram_sum{application="spring-boot-micrometer-tutorial",region="us-east"} 61.0
    // DistributionSummaryHistogram_max{application="spring-boot-micrometer-tutorial",region="us-east"} 51.0
    private DistributionSummary createDistributionSummary(MeterRegistry meterRegistry) {
        // Count how many times this API has been called
        return DistributionSummary.builder("DistributionSummaryHistogram")
                .description("A custom distribution summary for histogram")
                .maximumExpectedValue(100.0)
                .publishPercentileHistogram()
                .tag("region", "us-east")
                .register(meterRegistry);
    }
}
