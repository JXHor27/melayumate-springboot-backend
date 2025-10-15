package com.example.demo.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
 public class FileStorageController {

    @Autowired
    private final FileStorageService fileStorageService;

    /**
     * Handle file upload.
     *
     * @param file the uploaded file
     * @return a ResponseEntity containing the file URL or an error message
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // The @RequestParam("file") name must match the key in FormData
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileUrl = fileStorageService.uploadFile(file);
        return ResponseEntity.ok(fileUrl);
    }

    /**
     * Handle file deletion.
     *
     * @param objectKey the unique key of the file to delete
     * @return a ResponseEntity with a 204 No Content status indicating successful deletion
     */
    @DeleteMapping("/audio/{objectKey}")
    public ResponseEntity<String> deleteFile(@PathVariable String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return ResponseEntity.badRequest().body("File key is required");
        }
        // Reconstruct full key
        String fullObjectKey = "audio/" + objectKey;

        fileStorageService.deleteFile(fullObjectKey);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
