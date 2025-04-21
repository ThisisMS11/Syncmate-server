package com.SyncMate.SyncMate.repository;


import com.SyncMate.SyncMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>  {
    User findByEmail(String email);
}
