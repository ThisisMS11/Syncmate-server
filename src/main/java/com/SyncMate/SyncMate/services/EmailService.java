package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.enums.EmailStatus;
import com.SyncMate.SyncMate.repository.EmailRecordRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailRecord.getContact().getEmail());
            helper.setSubject(emailRecord.getSubject());
            helper.setText(emailRecord.getBody(), true);

            javaMailSender.send(mimeMessage);
            emailRecord.setStatus(EmailStatus.SENT);
            emailRecordRepository.save(emailRecord);
        } catch (Exception e) {
            log.error("Exception while SendEmail : ", e);
        }
    }
}
