package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.*;
import com.SyncMate.SyncMate.entity.Contact;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.UserException;
import com.SyncMate.SyncMate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveNewUser(RegisterRequest user){
        if(userRepository.findByEmail(user.getEmail()) != null) {
            log.error("User with the email already exists");
            throw UserException.userExists(user.getEmail());
        }
        User newUser = new User();
        log.info("Encoding the user password");
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRoles(Arrays.asList("USER"));
        userRepository.save(newUser);
    }

    public User getUserByEmail(String email) {
        log.info("Finding user with email : {}", email);
        User user =  userRepository.findByEmail(email);
        if(user == null){
            log.error("User with email {} not found", email);
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public List<UserContactsResponse> getUserContacts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByEmail(authentication.getName());

        log.info("Fetching user contacts");
        List<UserContactsResponse> contacts = user.getContacts().stream()
                .map(c -> new UserContactsResponse(
                        c.getId(),
                        c.getFirstName(),
                        c.getLastName(),
                        c.getGender(),
                        c.getMobile(),
                        c.getLinkedIn(),
                        c.getEmail(),
                        c.getPosition(),
                        c.getPositionType(),
                        c.getExperience(),
                        c.getValid(),
                        c.getCompany().getId(),
                        c.getCompany().getName(),
                        c.getCompany().getLogo()
                ))
                .collect(Collectors.toList());

        return contacts;
    }
}
