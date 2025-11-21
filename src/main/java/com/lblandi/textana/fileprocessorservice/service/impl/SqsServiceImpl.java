package com.lblandi.textana.fileprocessorservice.service.impl;

import com.lblandi.textana.fileprocessorservice.service.QueueService;
import com.lblandi.textana.fileprocessorservice.worker.FileProcessorWorker;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Slf4j
@Service
public class SqsServiceImpl implements QueueService {
    public static final int MAX_NUMBER_MESSAGES_SQS = 10;
    public static final int WAIT_TIME_SECONDS_SQS = 10;
    public static final int VISIBILITY_TIMEOUT_SQS = 30;
    private final SqsClient sqsClient;
    private final FileProcessorWorker fileProcessorWorker;

    @Value("${textana.aws.sqs.url}")
    private String queueUrl;

    public SqsServiceImpl(SqsClient sqsClient, FileProcessorWorker fileProcessorWorker) {
        this.sqsClient = sqsClient;
        this.fileProcessorWorker = fileProcessorWorker;
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    public void receiveNewFileIdentifier() {
        var request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(MAX_NUMBER_MESSAGES_SQS)
                .waitTimeSeconds(WAIT_TIME_SECONDS_SQS)
                .visibilityTimeout(VISIBILITY_TIMEOUT_SQS)
                .build();

        var messages = sqsClient.receiveMessage(request).messages();

        if (messages.isEmpty()) {
            return;
        }

        for (var msg : messages) {
            try {
                log.info("Trying to process file identifier: {}", msg.body());
                fileProcessorWorker.process(msg.body());
            } catch (Exception e) {
                log.error("Error processing message {}", msg.body(), e);
            } finally {
                sqsClient.deleteMessage(del -> del
                        .queueUrl(queueUrl)
                        .receiptHandle(msg.receiptHandle())
                );
            }
        }
    }
}
