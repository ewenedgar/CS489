package edu.miu.horelo.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor

public class FileDTO {
    private MultipartFile file; // Replace base64 with MultipartFile
    private String fileName;
}
