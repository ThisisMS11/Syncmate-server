package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.emailTemplates.EmailTemplateDto;
import com.SyncMate.SyncMate.dto.emailTemplates.RequestEmailTemplate;
import com.SyncMate.SyncMate.entity.EmailTemplate;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.EmailTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmailTemplateService {

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UtilService utilService;

    public EmailTemplateDto saveEmailTemplate(RequestEmailTemplate requestEmailTemplate) {
        try {
            log.info("Attempting to save email template: {}", requestEmailTemplate.getName());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.getUserByEmail(authentication.getName());
            log.info("Authenticated user: {}", user.getEmail());

            EmailTemplate emailTemplate = new EmailTemplate();
            emailTemplate.setName(requestEmailTemplate.getName());
            emailTemplate.setSubject(requestEmailTemplate.getSubject());
            emailTemplate.setBody(requestEmailTemplate.getBody());
            emailTemplate.setUser(user);

            emailTemplateRepository.save(emailTemplate);
            log.info("Email template '{}' saved successfully", emailTemplate.getName());

            EmailTemplateDto response = modelMapper.map(emailTemplate, EmailTemplateDto.class);
            log.info("Mapped EmailTemplate to EmailTemplateDto successfully");

            return response;
        } catch (Exception e) {
            log.error("Failed to save email template: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Failed to save email template:" + e.getMessage());
        }
    }

    public EmailTemplateDto getEmailTemplate(Long id) {
        try {
            log.info("Fetching email template with ID: {}", id);

            EmailTemplate emailTemplate = emailTemplateRepository.findById(id)
                    .orElseThrow(() -> CommonExceptions.resourceNotFound("Email template not found with ID: " + id));
            log.info("Email template found: {}", emailTemplate.getName());

            if (utilService.checkResourceAuthorization(emailTemplate.getUser())) {
                log.error("Forbidden Access for emailTemplate: {}", emailTemplate.getId());
                throw CommonExceptions.forbiddenAccess();
            }

            return modelMapper.map(emailTemplate, EmailTemplateDto.class);
        } catch (Exception e) {
            log.error("Failed to fetch email template: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Failed to fetch email template: " + e.getMessage());
        }
    }

    public EmailTemplateDto updateEmailTemplate(Long id, RequestEmailTemplate requestEmailTemplate) {
        try {
            log.info("Updating email template with ID: {}", id);

            EmailTemplate emailTemplate = emailTemplateRepository.findById(id)
                    .orElseThrow(() -> CommonExceptions.resourceNotFound("Email template not found with ID: " + id));

            if (utilService.checkResourceAuthorization(emailTemplate.getUser())) {
                log.error("Forbidden Access for emailTemplate: {}", emailTemplate.getId());
                throw CommonExceptions.forbiddenAccess();
            }

            emailTemplate.setName(requestEmailTemplate.getName());
            emailTemplate.setSubject(requestEmailTemplate.getSubject());
            emailTemplate.setBody(requestEmailTemplate.getBody());

            emailTemplateRepository.save(emailTemplate);
            log.info("Email template '{}' updated successfully", emailTemplate.getName());
            return modelMapper.map(emailTemplate, EmailTemplateDto.class);
        } catch (Exception e) {
            log.error("Failed to update email template: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Failed to update email template: " + e.getMessage());
        }
    }

    public void deleteEmailTemplate(Long id) {
        try {
            log.info("Deleting email template with ID: {}", id);

            EmailTemplate emailTemplate = emailTemplateRepository.findById(id)
                    .orElseThrow(() -> CommonExceptions.resourceNotFound("Email template not found with ID: " + id));

            if (utilService.checkResourceAuthorization(emailTemplate.getUser())) {
                log.error("Forbidden Access for emailTemplate: {}", emailTemplate.getId());
                throw CommonExceptions.forbiddenAccess();
            }

            emailTemplateRepository.delete(emailTemplate);
            log.info("Email template with ID '{}' deleted successfully", id);
        } catch (Exception e) {
            log.error("Failed to delete email template: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Failed to delete email template: " + e.getMessage());
        }
    }

    public List<EmailTemplateDto> getUserEmailTemplates() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.getUserByEmail(authentication.getName());
            List<EmailTemplate> emailTemplates = user.getEmailTemplates();

            return emailTemplates.stream()
                    .map(template -> modelMapper.map(template, EmailTemplateDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to fetch email templates: {}", e.getMessage(), e);
            throw CommonExceptions.operationFailed("Failed to fetch email templates: " + e.getMessage());
        }
    }
}
