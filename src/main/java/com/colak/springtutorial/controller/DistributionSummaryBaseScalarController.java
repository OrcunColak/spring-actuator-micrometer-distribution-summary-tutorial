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
@RequestMapping(path = "/basescalar")
@RequiredArgsConstructor
public class DistributionSummaryBaseScalarController {

    private DistributionSummary distributionSummary;

    @Autowired
    public void setRegistry(MeterRegistry registry) {
        distributionSummary = createDistributionSummary(registry);
    }

    // http://localhost:8080/basescalar/read
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

    // By default, if nothing is configured, the DistributionSummary will simply produce base scalar metrics,
    // returning just the count, total, and maximum values of the metric.

    // myDistributionSummary_count{application="spring-boot-micrometer-tutorial",region="us-east"} 2
    // myDistributionSummary_sum{application="spring-boot-micrometer-tutorial",region="us-east"} 74.0
    // myDistributionSummary_max{application="spring-boot-micrometer-tutorial",region="us-east"} 51.0
    private DistributionSummary createDistributionSummary(MeterRegistry meterRegistry) {
        // Count how many times this API has been called
        return DistributionSummary.builder("myDistributionSummary")
                .description("A custom distribution summary")
                .tag("region", "us-east")
                .register(meterRegistry);
    }
}
