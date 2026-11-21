package com.spirit.Citizen.services.user;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName;

    public GoogleCloudStorageService(@Value("${google.cloud.project-id}") String projectId,
                                     @Value("${google.cloud.bucket-name}") String bucketName,
                                     @Value("${google.application.credentials}") String credentialsBase64) throws IOException {
        this.bucketName = bucketName;

        // Décoder la chaîne Base64
        byte[] decodedCredentials = Base64.getDecoder().decode(credentialsBase64);

        try (ByteArrayInputStream credentialsStream = new ByteArrayInputStream(decodedCredentials)) {
            this.storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build()
                    .getService();
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new IllegalArgumentException("Le nom de fichier ne peut pas être nul");
        }

        String fileNameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));

        String newFileName = "CITIZEN___" + fileNameWithoutExtension + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                "_____preveus" + fileExtension;

        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, newFileName).build(),
                file.getBytes()
        );

        return blobInfo.getMediaLink();
    }

}

