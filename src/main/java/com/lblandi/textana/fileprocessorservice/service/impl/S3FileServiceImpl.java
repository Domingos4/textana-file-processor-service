package com.lblandi.textana.fileprocessorservice.service.impl;

import com.lblandi.textana.fileprocessorservice.exception.FileProcessingException;
import com.lblandi.textana.fileprocessorservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

            // build get object request
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileIdentifier)
                    .build();

            // retrieve content as stream
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);

            // read content with utf-8 encoding
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

    @Override
    public void deleteAllFiles() {

        // build list objects request
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listRes;

        // iterate over all pages, provided that there are more objects to delete
        do {

            // perform retrieval operation, if empty just exit the loop
            listRes = s3Client.listObjectsV2(listReq);

            if (listRes.contents().isEmpty()) break;

            // get object keys
            List<ObjectIdentifier> objectsToDelete = listRes.contents().stream()
                    .map(o -> ObjectIdentifier.builder().key(o.key()).build())
                    .toList();

            // build delete request with object keys
            DeleteObjectsRequest deleteReq = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(e -> e.objects(objectsToDelete))
                    .build();

            // perform deletion
            s3Client.deleteObjects(deleteReq);

            // iterate over next page
            listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .continuationToken(listRes.nextContinuationToken())
                    .build();

        } while (Boolean.TRUE.equals(listRes.isTruncated()));

        log.info("All files deleted from S3 bucket");
    }
}
