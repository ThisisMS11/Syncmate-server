package com.SyncMate.SyncMate.entity;
import com.SyncMate.SyncMate.enums.EmailStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "emails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    private String body;

    @Column(nullable = false)
    private Long scheduledTime;

    @Enumerated(EnumType.STRING)
    private EmailStatus status = EmailStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "contactId")
    @JsonBackReference
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

    @ManyToMany
    @JsonManagedReference
    @JoinTable(
            name = "email_attachments",
            joinColumns = @JoinColumn(name = "email_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<File> attachmentsList;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
