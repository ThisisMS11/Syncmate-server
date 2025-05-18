package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.enums.ExpiryBucket;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UtilService {
    public long bucketToExpiryTimestamp(ExpiryBucket expiryBucket) {
        Instant now = Instant.now();
        Instant expiry = switch (expiryBucket) {
            case ONE_WEEK -> now.plus(7, ChronoUnit.DAYS);
            case ONE_MONTH -> now.plus(30, ChronoUnit.DAYS);
            case THREE_MONTHS -> now.plus(90, ChronoUnit.DAYS);
            case ONE_YEAR -> now.plus(365, ChronoUnit.DAYS);
        };
        return expiry.toEpochMilli();
    }
}
