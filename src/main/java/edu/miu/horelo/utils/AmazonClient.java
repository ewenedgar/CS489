package edu.miu.horelo.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
@Component
public class AmazonClient {

    private static final Logger logger = LoggerFactory.getLogger(AmazonClient.class);

    private AmazonS3 s3client;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;

    @PostConstruct
    private void initializeAmazonClient() {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            this.s3client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.US_EAST_1)
                    .build();
            createBucket();
        } catch (Exception e) {
            logger.error("Failed to initialize AmazonClient", e);
            throw new RuntimeException("Failed to initialize AmazonClient", e);
        }
    }
    public void uploadFileToBucket(String fileName, File file, String folderToUpload) {
        logger.info("Uploading file {} to {}", fileName, folderToUpload);
        s3client.putObject(new PutObjectRequest(bucketName, folderToUpload + "/" + fileName, file));
    }

    public void deleteFileFromBucket(String filename, String folderName) {
        logger.info("Deleting file {} from {}", filename, folderName);
        DeleteObjectRequest delObjReq = new DeleteObjectRequest(bucketName, folderName + "/" + filename);
        s3client.deleteObject(delObjReq);
    }

    public void deleteMultipleFilesFromBucket(List<String> files) {
        DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucketName)
                .withKeys(files.toArray(new String[0]));
        logger.info("Deleting files...");
        s3client.deleteObjects(delObjReq);
    }

    public File getFileFromBucket(String filename, String folderName) {
        InputStream inputStream = getFileInputStream(filename, folderName);
        File file = new File(filename);
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return file;
        }
        return file;
    }

    public InputStream getFileInputStream(String filename, String folderName) {
        S3Object s3object = s3client.getObject(bucketName, folderName + "/" + filename);
        return s3object.getObjectContent();
    }

    private void createBucket() {
        if (s3client.doesBucketExistV2(bucketName)) {
            logger.info("Bucket {} already exists", bucketName);
            return;
        }
        try {
            logger.info("Creating bucket {}", bucketName);
            s3client.createBucket(bucketName);
        } catch (Exception e) {
            logger.error((ExceptionUtils.getStackTrace(e)));
        }
    }

    public String getFileUrl(String generatedFileName, String targetFolder) {
        // Construct the full S3 object key (path)
        String key = targetFolder + "/" + generatedFileName;

        // Check if the key exists, to prevent generating URLs for non-existent files
        if (!s3client.doesObjectExist(bucketName, key)) {
            throw new IllegalArgumentException("The file does not exist in the specified folder: " + key);
        }

        // Option 1: Generating a public URL (assuming the object is publicly accessible)
        return s3client.getUrl(bucketName, key).toString();

        // Option 2: If the file is private, generate a pre-signed URL with expiration (Optional)
        /*
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // URL valid for 1 hour
        GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL presignedUrl = s3Client.generatePresignedUrl(presignedUrlRequest);
        return presignedUrl.toString();
        */

    }
}
