package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
}
