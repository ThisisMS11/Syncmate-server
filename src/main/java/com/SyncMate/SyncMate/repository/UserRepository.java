package com.SyncMate.SyncMate.repository;


import com.SyncMate.SyncMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
