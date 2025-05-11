package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.email.EmailDto;
import com.SyncMate.SyncMate.entity.Contact;
import com.SyncMate.SyncMate.entity.EmailRecord;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.EmailRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    public void saveEmail(EmailDto emailDto) {
        // Validate the EmailDto
        log.info("Starting to save email with subject: {}", emailDto.getSubject());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        var violations = validator.validate(emailDto);
        if (!violations.isEmpty()) {
            for (var violation : violations) {
                log.error("Validation error for field {}: {}", violation.getPropertyPath(), violation.getMessage());
            }
            throw new IllegalArgumentException("Validation failed");
        }

        if (emailDto.getId() == null) {
            log.info("Creating new email");
            createEmail(emailDto);
        } else {
            log.info("Updating existing email with ID: {}", emailDto.getId());
            updateEmail(emailDto);
        }
    }

    private void createEmail(EmailDto emailDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        EmailRecord emailRecord = new EmailRecord();
        emailRecord.setSubject(emailDto.getSubject());
        emailRecord.setBody(emailDto.getBody());
        Contact contact = contactService.findContactById(emailDto.getContactId());
        emailRecord.setContact(contact);
        emailRecord.setScheduledTime(emailDto.getScheduledTime());
        emailRecord.setUser(user);

        try {
            emailRepository.save(emailRecord);
            log.info("Successfully created emailRecord with ID: {}", emailRecord.getId());
        } catch (DataAccessException ex) {
            log.error("Database error while saving emailRecord: {}", ex.getMessage(), ex);
            throw CommonExceptions.operationFailed("Saving emailRecord to database");
        }
    }

    private void updateEmail(EmailDto emailDto) {
        EmailRecord existingEmailRecord = emailRepository.findById(emailDto.getId())
                .orElseThrow(() -> {
                    log.error("EmailRecord Entry with ID {} not found", emailDto.getId());
                    return CommonExceptions.resourceNotFound(String.valueOf(emailDto.getId()));
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        if (!existingEmailRecord.getUser().getId().equals(user.getId())) {
            log.error("Forbidden Access for email: {}", emailDto.getId());
            throw CommonExceptions.forbiddenAccess();
        }

        existingEmailRecord.setSubject(emailDto.getSubject());
        existingEmailRecord.setBody(emailDto.getBody());
        Contact contact = contactService.findContactById(emailDto.getContactId());
        existingEmailRecord.setContact(contact);
        existingEmailRecord.setScheduledTime(emailDto.getScheduledTime());

        try {
            emailRepository.save(existingEmailRecord);
            log.info("Successfully updated email with ID: {}", existingEmailRecord.getId());
        } catch (DataAccessException ex) {
            log.error("Database error while updating email: {}", ex.getMessage(), ex);
            throw CommonExceptions.operationFailed("Updating email in database");
        }
    }
}
