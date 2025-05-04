package com.SyncMate.SyncMate.services;
import com.SyncMate.SyncMate.dto.email.EmailDto;
import com.SyncMate.SyncMate.entity.Contact;
import com.SyncMate.SyncMate.entity.Email;
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

        Email email = new Email();
        email.setSubject(emailDto.getSubject());
        email.setBody(emailDto.getBody());
        Contact contact = contactService.findContactById(emailDto.getContactId());
        email.setContact(contact);
        email.setScheduledTime(emailDto.getScheduledTime());
        email.setUser(user);

        try {
            emailRepository.save(email);
            log.info("Successfully created email with ID: {}", email.getId());
        } catch (DataAccessException ex) {
            log.error("Database error while saving email: {}", ex.getMessage(), ex);
            throw CommonExceptions.operationFailed("Saving email to database");
        }
    }

    private void updateEmail(EmailDto emailDto) {
        Email existingEmail = emailRepository.findById(emailDto.getId())
                .orElseThrow(() -> {
                    log.error("Email Entry with ID {} not found", emailDto.getId());
                    return CommonExceptions.resourceNotFound(String.valueOf(emailDto.getId()));
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        if (!existingEmail.getUser().getId().equals(user.getId())) {
            log.error("Forbidden Access for email: {}", emailDto.getId());
            throw CommonExceptions.forbiddenAccess();
        }

        existingEmail.setSubject(emailDto.getSubject());
        existingEmail.setBody(emailDto.getBody());
        Contact contact = contactService.findContactById(emailDto.getContactId());
        existingEmail.setContact(contact);
        existingEmail.setScheduledTime(emailDto.getScheduledTime());

        try {
            emailRepository.save(existingEmail);
            log.info("Successfully updated email with ID: {}", existingEmail.getId());
        } catch (DataAccessException ex) {
            log.error("Database error while updating email: {}", ex.getMessage(), ex);
            throw CommonExceptions.operationFailed("Updating email in database");
        }
    }
}
