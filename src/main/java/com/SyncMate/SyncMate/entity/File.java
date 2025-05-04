package com.SyncMate.SyncMate.entity;

import com.SyncMate.SyncMate.enums.FileType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original file name uploaded by the user
    private String originalFilename;

    // Unique name saved in GCS (e.g., UUID_originalFilename)
    private String gcsFilename;

    private String publicUrl;

    private String contentType;

    private Long size;

    private String bucketName;

    @Enumerated(EnumType.STRING)
    private FileType fileType = FileType.UNKNOWN;

    @ManyToMany(mappedBy = "attachmentsList")
    @JsonBackReference("email-files")
    private List<Email> emails;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference("user-files")
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
