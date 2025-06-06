package com.SyncMate.SyncMate.dto;

import com.SyncMate.SyncMate.enums.Gender;
import com.SyncMate.SyncMate.enums.PositionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContactDto {
    private Long id;

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    private String linkedIn;

    @NotBlank(message = "EmailRecord is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Position Type is required")
    private PositionType positionType = PositionType.OTHERS;

    @NotNull(message = "Experience is required")
    private Integer experience;

    @NotNull(message = "Valid flag is required")
    private Boolean valid;

    @NotNull(message = "CompanyID is required")
    private Long companyId;

    @NotNull(message = "Company name is required")
    private String companyName;

    @NotNull(message = "logo is required")
    private String logo;
}
