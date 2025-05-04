package com.SyncMate.SyncMate.repository;

import com.SyncMate.SyncMate.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {

}
