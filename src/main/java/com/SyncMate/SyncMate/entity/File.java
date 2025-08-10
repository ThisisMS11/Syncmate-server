package com.SyncMate.SyncMate.entity;

import com.SyncMate.SyncMate.enums.FileType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "files")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "originalFilename", "publicId"})
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;

    private String publicId;

    private String url;

    private String secureUrl;

    private String format;

    private Long bytes;

    @Enumerated(EnumType.STRING)
    private FileType fileType = FileType.UNKNOWN;

    @ManyToMany(mappedBy = "attachmentsList", fetch = FetchType.LAZY)
    @JsonBackReference("email-files")
    private List<EmailRecord> emailRecords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonBackReference("user-files")
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", originalFilename='" + originalFilename + '\'' +
                ", publicId='" + publicId + '\'' +
                ", url='" + url + '\'' +
                ", secureUrl='" + secureUrl + '\'' +
                ", format='" + format + '\'' +
                ", bytes=" + bytes +
                ", fileType=" + fileType +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
