package edu.miu.horelo.controller;

import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.service.FileManagerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static edu.miu.horelo.controller.UserAuthController.getSavedFileDTOResponseEntity;


@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileManagerService fileManagerService;
private final String imageFolder = "uploads";
    @PostMapping("/file/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            // You can customize the storage path here
            String uploadDir = "uploads/";
            File destinationFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(destinationFile);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        byte[] imageData = fileManagerService.getFileAsBytes(fileName, imageFolder); // Replace with your actual logic
        String mediaType = getFileMediaType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaType))
                .body(imageData);
    }

    @DeleteMapping("/files/{fileName:.+}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileName") String fileName) {
        fileManagerService.deleteFile(fileName, imageFolder);
        return ResponseEntity.noContent().build();
    }

    private String getFileMediaType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default media type if there's no extension
        }

        String mediaType;
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        mediaType = switch (fileExtension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "pdf" -> MediaType.APPLICATION_PDF_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "txt" -> MediaType.TEXT_PLAIN_VALUE;
            case "html" -> MediaType.TEXT_HTML_VALUE;
            case "json" -> MediaType.APPLICATION_JSON_VALUE;
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default media type for unknown types
        };

        return mediaType;
    }
    @PostMapping("/upload")
    public ResponseEntity<SavedFileDTO> uploadFileTwo(@RequestParam("file") MultipartFile file) {
        return getSavedFileDTOResponseEntity(file, fileManagerService, imageFolder);
    }
}
