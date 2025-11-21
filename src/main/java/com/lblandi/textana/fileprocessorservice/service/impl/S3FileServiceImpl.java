package com.lblandi.textana.fileprocessorservice.service.impl;

import com.lblandi.textana.fileprocessorservice.exception.FileProcessingException;
import com.lblandi.textana.fileprocessorservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
public class S3FileServiceImpl implements FileService {
    private final S3Client s3Client;

    @Value("${textana.aws.s3.bucket}")
    private String bucketName;

    public S3FileServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String getFileContent(String fileIdentifier) {
        try {
            log.info("Retrieving content for file: {}", fileIdentifier);

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileIdentifier)
                    .build();

            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response, StandardCharsets.UTF_8))
            ) {
                return reader.lines().collect(Collectors.joining("\n"));
            }

        } catch (Exception e) {
            log.error("Error retrieving content for file: {}", fileIdentifier, e);
            throw new FileProcessingException(fileIdentifier);
        }
    }
}
