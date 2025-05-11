package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.EmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailRecord, Long> {
}
