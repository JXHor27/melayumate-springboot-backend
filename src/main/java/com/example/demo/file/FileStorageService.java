package com.example.demo.file;

import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.net.URL;

/**
 * Service class for handling file uploads and deletions in an S3 bucket.
 */
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final Log logger = LogFactory.getLog(FileStorageService.class);

    @Autowired
    private final S3Client s3Client;

    @Autowired
    private final IdGenerator idGenerator;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Uploads a file to the S3 bucket and returns the unique object key.
     * @param file The file to upload.
     * @return The unique object key of the uploaded file (e.g., "audio/xyz.wav")
     */
    public String uploadFile(MultipartFile file) {
        try {
            logger.info("Attempting to upload file to S3: " + file.getOriginalFilename());

            // 1. Define the folder prefix. Make sure it ends with a slash.
            String folder = "audio/";

            // 2. Generate the unique filename.
            String uniqueFileName = idGenerator.generateAudioFileId() + "_" + file.getOriginalFilename();

            // 3. Combine the folder and filename to create the full object key.
            String objectKey = folder + uniqueFileName;

            // Build the request to upload the file
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ) // Make the file publicly readable
                    .build();

            // Upload the file to S3
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Get the public URL of the uploaded object
            URL fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(objectKey));

            logger.info("File uploaded successfully to S3. URL: " + fileUrl.toString());
            return objectKey;

        } catch (IOException e) {
            logger.error("Error uploading file: Could not read file data. " +  e);
            throw new RuntimeException("Error uploading file: Could not read file data.", e);
        } catch (S3Exception e) {
            logger.error("Error uploading file to S3: " + e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Error uploading file to S3.", e);
        }
    }

    /**
     * Deletes a file from the S3 bucket.
     * @param objectKey The unique key of the object to delete (e.g., "audio/xyz.wav")
     */
    public void deleteFile(String objectKey) {
        try {
            logger.info("Attempting to delete file from S3 with key: " + objectKey);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            logger.info("Successfully deleted file with key: " + objectKey);

        } catch (S3Exception e) {
            logger.error("Error deleting file from S3: " + e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
        catch (Exception e) {
            // Catch any other unexpected errors
            logger.error("An unexpected error occurred during S3 deletion of key: " + objectKey);
            throw new RuntimeException("An unexpected error occurred during S3 deletion", e);
        }
    }
}
