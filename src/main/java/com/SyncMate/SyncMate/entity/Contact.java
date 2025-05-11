package com.SyncMate.SyncMate.entity;

import com.SyncMate.SyncMate.enums.Gender;
import com.SyncMate.SyncMate.enums.PositionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contacts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "email", "mobile"})
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = true)
    private String linkedIn;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionType positionType = PositionType.OTHERS;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private Boolean valid = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @JsonBackReference("user-contacts")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    @JsonBackReference("contact-company")
    private Company company;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("contact-emailRecords")
    private List<EmailRecord> emailRecords;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", mobile='" + mobile + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", positionType=" + positionType +
                ", experience=" + experience +
                ", valid=" + valid +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
