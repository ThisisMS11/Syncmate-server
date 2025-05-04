package com.SyncMate.SyncMate.dto.file;
import com.SyncMate.SyncMate.enums.FileType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private Long id;

    @NotBlank(message = "fileName is required")
    private String fileName;

    @NotBlank(message = "fileUrl is required")
    private String fileUrl;

    private FileType fileType = FileType.UNKNOWN;
}
