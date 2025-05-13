package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.EmailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailRecordRepository extends JpaRepository<EmailRecord, Long> {
    @Query("SELECT e FROM EmailRecord e JOIN FETCH e.contact")
    List<EmailRecord> findAllWithContact();
}
