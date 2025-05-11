package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.User;
import com.SyncMate.SyncMate.entity.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {

    UserConfig findByUser(User user);
}
