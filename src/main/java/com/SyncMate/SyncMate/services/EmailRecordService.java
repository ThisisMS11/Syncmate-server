package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.email.EmailRecordDto;
import com.SyncMate.SyncMate.entity.Contact;
import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmailRecordService {
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ContactService contactService;

    public void saveEmailRecords(EmailRecordDto emailRecordDto) {
        // Validate the EmailRecordDto
        log.info("Starting to save email with subject: {}", emailRecordDto.getSubject());

        if (emailRecordDto.getId() == null) {
            log.info("Creating new email");
            createEmailsInBatch(emailRecordDto);
        }
    }

    private void createEmailsInBatch(EmailRecordDto emailRecordDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(auth.getName());

        if (emailRecordDto.getContactIds().isEmpty()) {
            log.error("Recipient List is empty");
            throw CommonExceptions.invalidRequest("Recipient list is empty");
        }

        List<Long> contactIds = emailRecordDto.getContactIds();
        List<Long> missingContacts = contactIds.stream()
                .filter(id -> !contactService.existsById(id))
                .toList();

        List<Long> missingAttachments = emailRecordDto.getAttachmentIds().stream()
                .filter(id -> !fileService.existsById(id))
                .toList();

        if (!missingContacts.isEmpty() || !missingAttachments.isEmpty()) {
            String errorMsg = String.format("Missing contacts: %s | Missing attachments: %s",
                    missingContacts, missingAttachments);
            throw CommonExceptions.resourceNotFound(errorMsg);
        }

        log.info("[createEmailsInBatch] Fetching files: {}", emailRecordDto.getAttachmentIds());
        List<File> attachments = emailRecordDto.getAttachmentIds().stream()
                .map(fileService::getFileById)
                .toList();

        List<Contact> contacts = emailRecordDto.getContactIds().stream()
                .map(contactService::findContactById)
                .toList();

        List<EmailRecord> records = contacts.stream()
                .map(contact -> buildEmailRecord(emailRecordDto, user, contact, attachments))
                .toList();

        emailRepository.saveAll(records);
        log.info("Saved {} email records", records.size());
    }
//    private void updateEmail(EmailRecordDto emailDto) {
//        EmailRecord existingEmailRecord = emailRepository.findById(emailDto.getId())
//                .orElseThrow(() -> {
//                    log.error("EmailRecord Entry with ID {} not found", emailDto.getId());
//                    return CommonExceptions.resourceNotFound(String.valueOf(emailDto.getId()));
//                });
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User user = userService.getUserByEmail(authentication.getName());
//
//        if (!existingEmailRecord.getUser().getId().equals(user.getId())) {
//            log.error("Forbidden Access for email: {}", emailDto.getId());
//            throw CommonExceptions.forbiddenAccess();
//        }
//
//        existingEmailRecord.setSubject(emailDto.getSubject());
//        existingEmailRecord.setBody(emailDto.getBody());
//        Contact contact = contactService.findContactById(emailDto.getContactId());
//        existingEmailRecord.setContact(contact);
//        existingEmailRecord.setScheduledTime(emailDto.getScheduledTime());
//
//        try {
//            emailRepository.save(existingEmailRecord);
//            log.info("Successfully updated email with ID: {}", existingEmailRecord.getId());
//        } catch (DataAccessException ex) {
//            log.error("Database error while updating email: {}", ex.getMessage(), ex);
//            throw CommonExceptions.operationFailed("Updating email in database");
//        }
//    }

    private EmailRecord buildEmailRecord(EmailRecordDto dto, User user, Contact contact, List<File> attachments) {
        log.info("Creating Email Record for contact : {} ",contact.getEmail());
        EmailRecord record = new EmailRecord();
        record.setSubject(dto.getSubject());
        record.setBody(dto.getBody());
        record.setScheduledTime(dto.getScheduledTime());
        record.setUser(user);
        record.setContact(contact);
        record.setAttachmentsList(attachments);
        return record;
    }

    public List<EmailRecord> getUserEmailRecords() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(auth.getName());
        return user.getEmailRecords();
    }
}
