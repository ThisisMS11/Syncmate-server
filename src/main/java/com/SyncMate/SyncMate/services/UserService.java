package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.RegisterRequest;
import com.SyncMate.SyncMate.dto.UserContactsDto;
import com.SyncMate.SyncMate.entity.File;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.enums.Role;
import com.SyncMate.SyncMate.exception.UserException;
import com.SyncMate.SyncMate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    UserRepository userRepository;


    public void saveNewUser(RegisterRequest user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            log.error("User with the email already exists");
            throw UserException.userExists(user.getEmail());
        }
        User newUser = new User();
        log.info("Encoding the user password");
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.addRole(Role.USER);
        userRepository.save(newUser);
    }

    public User getUserByEmail(String email) {
        log.info("Finding user with email : {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User with email {} not found", email);
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public List<UserContactsDto> getUserContacts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByEmail(authentication.getName());

        log.info("Fetching user contacts");
        List<UserContactsDto> contacts = user.getContacts().stream()
                .map(c -> new UserContactsDto(
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

    public List<File> getUserFiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserByEmail(authentication.getName());
        log.info("Fetching user files");
        return user.getFiles();
    }
}
