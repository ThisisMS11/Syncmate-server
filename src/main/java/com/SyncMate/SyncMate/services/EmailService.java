package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.enums.EmailStatus;
import com.SyncMate.SyncMate.repository.EmailRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailRecordRepository emailRecordRepository;

    public void sendEmail(EmailRecord emailRecord) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(emailRecord.getContact().getEmail());
            mail.setSubject(emailRecord.getSubject());
            mail.setText(emailRecord.getBody());
            javaMailSender.send(mail);
            emailRecord.setStatus(EmailStatus.SENT);
            emailRecordRepository.save(emailRecord);
        } catch (Exception e) {
            log.error("Exception while SendEmail : ", e);
        }
    }
}
