package com.SyncMate.SyncMate.services;

import com.SyncMate.SyncMate.dto.TokenResponse;
import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.entity.UserConfig;
import com.SyncMate.SyncMate.repository.UserConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserConfigService {

    @Autowired
    private UserConfigRepository userConfigRepository;
    @Autowired
    private UserService userService;

    public void saveUserConfig(TokenResponse tokenResponse, String email) {

        User user = userService.getUserByEmail(email);

        UserConfig userConfig = userConfigRepository.findByUser(user);
        if (userConfig == null) {
            log.info("No existing UserConfig found. Creating new for user '{}'.", email);
            userConfig = new UserConfig();
            userConfig.setUser(user);
        }

        boolean updated = false;

        if (tokenResponse.getAccessToken() != null) {
            userConfig.setAccess_token(tokenResponse.getAccessToken());
            updated = true;
        }
        if (tokenResponse.getRefreshToken() != null) {
            userConfig.setRefresh_token(tokenResponse.getRefreshToken());
            updated = true;
        }

        if (updated) {
            userConfigRepository.save(userConfig);
            log.info("UserConfig saved successfully for user '{}'.", email);
        } else {
            log.info("No updates made to UserConfig for user '{}'.", email);
        }
    }
}
