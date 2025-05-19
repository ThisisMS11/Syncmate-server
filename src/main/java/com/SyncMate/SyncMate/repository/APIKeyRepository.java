package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.APIkey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface APIKeyRepository extends JpaRepository<APIkey, Long> {

    APIkey findByApiKeyHash(String apiKeyHash);

    @Query("SELECT e FROM APIkey e JOIN FETCH e.user WHERE e.apiKeyHash = :apiKey")
    APIkey findUserWithApiKey(@Param("apiKey") String apiKey);

}
