package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.APIkey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface APIKeyRepository extends JpaRepository<APIkey, Long> {
}
