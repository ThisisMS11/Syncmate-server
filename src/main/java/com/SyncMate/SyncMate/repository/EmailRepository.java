package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email,Long> {
}
