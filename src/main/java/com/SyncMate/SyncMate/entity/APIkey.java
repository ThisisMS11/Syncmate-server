package com.SyncMate.SyncMate.entity;

import com.SyncMate.SyncMate.enums.PartnerLevel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.common.value.qual.MinLen;
import org.hibernate.Length;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "apiKeys")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class APIkey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String apiKeyHash;

    @Column(nullable = false)
    @Enumerated
    private PartnerLevel level = PartnerLevel.EXTENSION;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonBackReference("user-api-keys")
    private User user;

    @Column(nullable = false)
    private Long expiryTimestamp;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
