package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.ContactRequestDto;
import com.SyncMate.SyncMate.dto.UserContactDto;
import com.SyncMate.SyncMate.entity.Company;
import com.SyncMate.SyncMate.entity.Contact;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.CommonExceptions;
import com.SyncMate.SyncMate.repository.CompanyRepository;
import com.SyncMate.SyncMate.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    public UserContactDto createContact(ContactRequestDto contactInfo) {
        // Logic for creating a new contact
        log.info("Creating a new contact for email: {}", contactInfo.getEmail());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Finding user with name : {} ", authentication.getName());
        User user = userService.getUserByEmail(authentication.getName());

        log.info("Checking unique email constraint");
        Contact contact = contactRepository.findByEmail(contactInfo.getEmail());

        if (contact != null) {
            System.out.println(contact);
            throw CommonExceptions.inputFieldValueAlreadyExists(contactInfo.getEmail());
        }

        contact = new Contact();
        // Set fields
        contact.setFirstName(contactInfo.getFirstName());
        contact.setLastName(contactInfo.getLastName());
        contact.setGender(contactInfo.getGender());
        contact.setMobile(contactInfo.getMobile());
        contact.setLinkedIn(contactInfo.getLinkedIn());
        contact.setEmail(contactInfo.getEmail());
        contact.setPosition(contactInfo.getPosition());
        contact.setPositionType(contactInfo.getPositionType());
        contact.setExperience(contactInfo.getExperience());
        contact.setValid(true);
        contact.setUser(user);

        Company company = companyRepository.findById(contactInfo.getCompanyId())
                .orElseThrow(() -> {
                    log.error("Company with ID {} not found", contactInfo.getCompanyId());
                    return CommonExceptions.resourceNotFound(String.valueOf(contactInfo.getCompanyId()));
                });

        contact.setCompany(company);

        // Save to repository
        try {
            contactRepository.save(contact);
            log.info("Successfully created contact with ID something is there: {}", contact.getId());
            UserContactDto userContactDto = modelMapper.map(contact, UserContactDto.class);
            userContactDto.setLogo(contact.getCompany().getLogo());
            return userContactDto;
        } catch (DataAccessException ex) {
            log.error("Database error while saving contact: {}", ex.getMessage(), ex);
            throw CommonExceptions.operationFailed("Saving contact into database");
        }
    }

    public UserContactDto updateContact(Long id, ContactRequestDto contactInfo) {
        log.info("Updating contact with ID: {}", id);

        // Fetch existing contact from DB
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contact with ID {} not found", id);
                    return CommonExceptions.resourceNotFound(String.valueOf(id));
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        // Check for forbidden access (ensure contact belongs to the user)
        if (!existingContact.getUser().getId().equals(user.getId())) {
            log.error("Forbidden Access for contact: {}", id);
            throw CommonExceptions.forbiddenAccess();
        }

        // Update fields from the DTO
        existingContact.setFirstName(contactInfo.getFirstName());
        existingContact.setLastName(contactInfo.getLastName());
        existingContact.setGender(contactInfo.getGender());
        existingContact.setMobile(contactInfo.getMobile());
        existingContact.setLinkedIn(contactInfo.getLinkedIn());
        existingContact.setEmail(contactInfo.getEmail());
        existingContact.setPosition(contactInfo.getPosition());
        existingContact.setPositionType(contactInfo.getPositionType());
        existingContact.setExperience(contactInfo.getExperience());
        existingContact.setValid(contactInfo.getValid());

        // Only update company if it has changed
        if (existingContact.getCompany() == null || !existingContact.getCompany().getId().equals(contactInfo.getCompanyId())) {
            log.info("Company ID has changed for contact with ID: {}. Updating company.", contactInfo.getId());

            Company company = companyRepository.findById(contactInfo.getCompanyId())
                    .orElseThrow(() -> {
                        log.error("Company with ID {} not found during update", contactInfo.getCompanyId());
                        return CommonExceptions.resourceNotFound(String.valueOf(contactInfo.getCompanyId()));
                    });

            existingContact.setCompany(company);


        }

        contactRepository.save(existingContact);
        log.info("Successfully updated contact with ID: {}", existingContact.getId());
        UserContactDto userContactDto = modelMapper.map(existingContact, UserContactDto.class);
        userContactDto.setLogo(existingContact.getCompany().getLogo());
        return userContactDto;
    }

    public Contact findContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contact with ID {} not found", id);
                    return CommonExceptions.resourceNotFound(String.valueOf(id));
                });
    }

    public boolean existsById(Long id) {
        log.info("Validating the existence of contact with id : {}", id);
        return contactRepository.existsById(id);
    }

    public void deleteContact(Long id) {
        log.info("Starting to delete contact with ID : {}", id);
        Contact contact = findContactById(id);

        if (contact == null) {
            log.error("Contact with id not found : {}", id);
            throw CommonExceptions.resourceNotFound(String.valueOf(id));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        if (!contact.getUser().equals(user)) {
            log.error("Not Authorized to delete contact with id : {}", id);
            throw CommonExceptions.forbiddenAccess();
        }

        try {
            log.info("Deleting contact with id: {}", id);
            contactRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete contact with id: {}. Error: {}", id, e.getMessage());
            throw CommonExceptions.operationFailed(e.getMessage());
        }
    }
}

