package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact,Long> {
    Contact findByEmail(String email);
}
