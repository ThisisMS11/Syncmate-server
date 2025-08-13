package com.SyncMate.SyncMate.scheduler;

import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.enums.EmailStatus;
import com.SyncMate.SyncMate.repository.EmailRecordRepository;
import com.SyncMate.SyncMate.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EmailScheduler {

    @Autowired
    private EmailRecordRepository emailRecordRepository;

    @Autowired
    private EmailService emailService;

//    @Scheduled(cron = "0 * * * * *")
    public void SendScheduledEmails() {
        log.info("Starting send email scheduler...");

        List<EmailRecord> emailRecordList = emailRecordRepository.findAllWithContact();

        if (emailRecordList.isEmpty()) {
            log.info("No Email records found");
            return;
        }

        List<EmailRecord> pendingEmailRecordList = emailRecordList.stream().filter((emailRecord ->
                EmailStatus.PENDING.equals(emailRecord.getStatus())
                        && emailRecord.getScheduledTime() <= System.currentTimeMillis()
        )).toList();

        if (pendingEmailRecordList.isEmpty()) {
            log.info("No pending email records to send.");
            return;
        }
        log.info("Found {} pending email(s) to process.", pendingEmailRecordList.size());

        pendingEmailRecordList.forEach(emailRecord -> {
            try {
                emailService.sendEmail(emailRecord);
                log.info("Successfully sent email with ID: {}", emailRecord.getId());
            } catch (Exception e) {
                log.error("Failed to send email with ID: {}. Error: {}", emailRecord.getId(), e.getMessage(), e);
            }
        });
    }
}
