package edu.miu.horelo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedFileDTO {

    private String originalFileName;
    private String generatedFileName;
    private Date uploadedAt;
    private String  fileUrl;
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }


    public void setGeneratedFileName(String generatedFileName) {
        this.generatedFileName = generatedFileName;
    }


    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setFileUrl(String fileUrl) {
    }
}
