package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.exception.UserException;
import com.SyncMate.SyncMate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void saveNewUser(User user){
        if(userRepository.findByEmail(user.getEmail()) != null){
            log.error("User with the email already exists");
            throw UserException.userExists(user.getEmail());
        }

        log.info("Encoding the user password");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);
    }
}
