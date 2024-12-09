package edu.miu.horelo.service;

import com.amazonaws.util.IOUtils;
import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.utils.AmazonClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class FileManagerService {

    private static final String UPLOAD_FOLDER_NAME = "public-files"; // Default folder name
    private final AmazonClient amazonClient;

    @Autowired
    FileManagerService(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    public void deleteFile(String fileName, String folderName) {
        String targetFolder = (folderName != null && !folderName.isEmpty()) ? folderName : UPLOAD_FOLDER_NAME;
        amazonClient.deleteFileFromBucket(fileName, targetFolder);
    }

    public byte[] getFileAsBytes(String fileName, String folderName) {
        String targetFolder = (folderName != null && !folderName.isEmpty()) ? folderName : UPLOAD_FOLDER_NAME;
        InputStream inputStream = amazonClient.getFileInputStream(fileName, targetFolder);
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private String generateFileName(FileDTO fileDTO) {
        String name = fileDTO.getFileName().replaceAll("[^a-zA-Z0-9.-]", "_");
        return (new Date().getTime() + "_" + name);
    }

    public SavedFileDTO uploadFile(FileDTO fileDTO, String folderName, String oldFileName) {
        SavedFileDTO savedFile = new SavedFileDTO();

        // Automatically get the file name from the MultipartFile
        String originalFileName = fileDTO.getFileName();
        savedFile.setOriginalFileName(originalFileName);

        // Generate a file name
        String generatedFileName = generateFileName(fileDTO);
        savedFile.setGeneratedFileName(generatedFileName);

        // Convert MultipartFile to a File object for upload
        MultipartFile file = fileDTO.getFile();
        File tempFile = convertMultipartFileToFile(file);

        // Use the provided folderName or fall back to the default UPLOAD_FOLDER_NAME
        String targetFolder = (folderName != null && !folderName.isEmpty()) ? folderName : UPLOAD_FOLDER_NAME;

        // Upload the file to S3 or any other storage
        amazonClient.uploadFileToBucket(savedFile.getGeneratedFileName(), tempFile, targetFolder);

        // Retrieve and set the URL of the uploaded file
        String fileUrl = amazonClient.getFileUrl(savedFile.getGeneratedFileName(), targetFolder);
        savedFile.setFileUrl(fileUrl);
        savedFile.setUploadedAt(new Date());

        // Delete the old file if it exists
        if (oldFileName != null && !oldFileName.isEmpty()) {
            amazonClient.deleteFileFromBucket(oldFileName, targetFolder);
        }

        // Clean up temporary file after upload
        try {
            FileUtils.forceDelete(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return savedFile;
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedFile;
    }
}
