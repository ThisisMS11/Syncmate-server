package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.constants.Placeholders;
import com.SyncMate.SyncMate.dto.email.EmailRecordRequestDto;
import com.SyncMate.SyncMate.entity.Contact;
import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.EmailRecordRepository;
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
    private EmailRecordRepository emailRecordRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ContactService contactService;

    public List<EmailRecord> saveEmailRecords(EmailRecordRequestDto emailRecordDto) {
        log.info("Starting to save email with subject: {}", emailRecordDto.getSubject());

        if (emailRecordDto.getId() == null) {
            log.info("Creating new email");
            return createEmailsInBatch(emailRecordDto);
        }
        return null;
    }

    private List<EmailRecord> createEmailsInBatch(EmailRecordRequestDto emailRecordRequestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(auth.getName());

        if (emailRecordRequestDto.getContactIds().isEmpty()) {
            log.error("Recipient List is empty");
            throw CommonExceptions.invalidRequest("Recipient list is empty");
        }

        List<Long> contactIds = emailRecordRequestDto.getContactIds();
        List<Long> missingContacts = contactIds.stream()
                .filter(id -> !contactService.existsById(id))
                .toList();

        List<Long> missingAttachments = emailRecordRequestDto.getAttachmentIds().stream()
                .filter(id -> !fileService.existsById(id))
                .toList();

        if (!missingContacts.isEmpty() || !missingAttachments.isEmpty()) {
            String errorMsg = String.format("Missing contacts: %s | Missing attachments: %s",
                    missingContacts, missingAttachments);
            throw CommonExceptions.resourceNotFound(errorMsg);
        }

        log.info("[createEmailsInBatch] Fetching files: {}", emailRecordRequestDto.getAttachmentIds());
        List<File> attachments = emailRecordRequestDto.getAttachmentIds().stream()
                .map(fileService::getFileById)
                .toList();

        List<Contact> contacts = emailRecordRequestDto.getContactIds().stream()
                .map(contactService::findContactById)
                .toList();

        List<EmailRecord> records = contacts.stream()
                .map(contact -> buildEmailRecord(emailRecordRequestDto, user, contact, attachments))
                .toList();

        try {
            List<EmailRecord> emailRecordList = emailRecordRepository.saveAll(records);
            log.info("Saved {} email records", records.size());
            return emailRecordList;
        } catch (Exception e) {
            throw CommonExceptions.operationFailed("Saving Email Records Failed" + e.getMessage());
        }
    }

    private EmailRecord buildEmailRecord(EmailRecordRequestDto dto, User user, Contact contact, List<File> attachments) {
        log.info("Creating Email Record for contact : {} ", contact.getEmail());
        EmailRecord record = new EmailRecord();
        String modifiedSubject = injectValuesIntoPlaceholders(dto.getSubject(), contact, dto.getAdditionalData());
        record.setSubject(modifiedSubject);
        String modifiedBody = injectValuesIntoPlaceholders(dto.getBody(), contact, dto.getAdditionalData());
        record.setBody(modifiedBody);
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

    private String injectValuesIntoPlaceholders(String body, Contact contact, EmailRecordRequestDto.AdditionalData additionalData) {
        if (body == null) return "";

        String genderPrefix = switch (contact.getGender()) {
            case MALE -> "Mr.";
            case FEMALE -> "Ms.";
            default -> "";
        };

        return body
                .replace(Placeholders.CONTACT_FIRST_NAME, contact.getFirstName())
                .replace(Placeholders.CONTACT_LAST_NAME, contact.getLastName())
                .replace(Placeholders.CONTACT_POSITION, contact.getPosition())
                .replace(Placeholders.CONTACT_COMPANY, contact.getCompany() != null ? contact.getCompany().getName() : "")
                .replace(Placeholders.CONTACT_EMAIL, contact.getEmail())
                .replace(Placeholders.CONTACT_MOBILE, contact.getMobile() != null ? contact.getMobile() : "")
                .replace(Placeholders.CONTACT_LINKEDIN, contact.getLinkedIn() != null ? contact.getLinkedIn() : "")
                .replace(Placeholders.CONTACT_GENDER, genderPrefix)
                .replace(Placeholders.CONTACT_EXPERIENCE, contact.getExperience() != null ? contact.getExperience().toString() : "")
                .replace(Placeholders.CONTACT_POSITION_TYPE, contact.getPositionType() != null ? contact.getPositionType().toString() : "")
                .replace(Placeholders.INTERNSHIP_LINK, additionalData != null && additionalData.getInternshipLink() != null ? additionalData.getInternshipLink() : "")
                .replace(Placeholders.RESUME_LINK, additionalData != null && additionalData.getResumeLink() != null ? additionalData.getResumeLink() : "")
                .replace(Placeholders.COVER_LETTER_LINK, additionalData != null && additionalData.getCoverLetterLink() != null ? additionalData.getCoverLetterLink() : "");
    }


}
