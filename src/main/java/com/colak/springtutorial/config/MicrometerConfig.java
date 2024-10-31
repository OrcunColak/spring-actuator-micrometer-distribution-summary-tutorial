package com.colak.springtutorial.config;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// This class shows that we can create prometheus objects globally
@Component

@Getter
@RequiredArgsConstructor
public class MicrometerConfig {
    private DistributionSummary amountSum;
    private final MeterRegistry registry;

    @PostConstruct
    private void init() {
        amountSum = registry.summary("order_amount_sum", "orderAmount", "test-svc");
    }
}
