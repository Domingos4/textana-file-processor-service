package com.lblandi.textana.fileprocessorservice.repository;

import com.lblandi.textana.fileprocessorservice.entity.AnalysisResultEntity;
import com.lblandi.textana.fileprocessorservice.entity.FileAnalysisEntity;
import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import com.lblandi.textana.fileprocessorservice.request.SaveAnalysisItemRequest;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class DynamoDbFileAnalysisRepository {
    private static final String TABLE_NAME = "textana-file-analysis";

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbFileAnalysisRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Optional<FileAnalysisEntity> findByIdentifier(String fileIdentifier) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("uuid", AttributeValue.fromS(fileIdentifier)))
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();

        if (item == null || item.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapToEntity(item));
    }

    public void saveAnalysis(SaveAnalysisItemRequest request) {
        Map<String, AttributeValue> item = new HashMap<>();

        item.put("uuid", AttributeValue.fromS(request.getFileIdentifier()));
        item.put("status", AttributeValue.fromS(request.getStatus().name()));
        item.put("lastStepAt", AttributeValue.fromS(request.getLastStepAt().toString()));

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

    public void updateAnalysis(String uuid, String resume, EmotionDetectedEnum emotionDetected) {
        Map<String, AttributeValueUpdate> updates = new HashMap<>();

        updates.put("resume", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(resume))
                .action(AttributeAction.PUT)
                .build());

        updates.put("emotionDetected", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(emotionDetected.name()))
                .action(AttributeAction.PUT)
                .build());

        updates.put("status", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(FileAnalysisStatusEnum.COMPLETED.name()))
                .action(AttributeAction.PUT)
                .build());

        updates.put("lastStepAt", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(LocalDateTime.now().toString()))
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("uuid", AttributeValue.fromS(uuid)))
                .attributeUpdates(updates)
                .build();

        dynamoDbClient.updateItem(request);
    }

    private FileAnalysisEntity mapToEntity(Map<String, AttributeValue> item) {
        AnalysisResultEntity result = null;

        if (item.containsKey("resume") && item.containsKey("emotionDetected")) {
            result = AnalysisResultEntity.builder()
                    .resume(item.get("resume").s())
                    .emotionDetected(EmotionDetectedEnum.valueOf(item.get("emotionDetected").s()))
                    .build();
        }

        return FileAnalysisEntity.builder()
                .uuid(item.get("uuid").s())
                .status(FileAnalysisStatusEnum.valueOf(item.get("status").s()))
                .result(result)
                .lastStepAt(LocalDateTime.parse(item.get("lastStepAt").s()))
                .build();
    }
}
