package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.ExpiryBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UtilService {

    @Autowired
    private UserService userService;

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

    public boolean checkResourceAuthorization(User resourceUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User sessionUser = userService.getUserByEmail(authentication.getName());
        return sessionUser.getId().equals(resourceUser.getId());
    }
}
