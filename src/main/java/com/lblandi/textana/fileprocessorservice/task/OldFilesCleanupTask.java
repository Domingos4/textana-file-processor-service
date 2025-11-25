package com.lblandi.textana.fileprocessorservice.task;

import com.lblandi.textana.fileprocessorservice.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OldFilesCleanupTask {
    private final FileService fileService;

    public OldFilesCleanupTask(FileService fileService) {
        this.fileService = fileService;
    }

    @Scheduled(cron = "${textana.scheduling.file-cleanup.cron}")
    void cleanupOldFiles() {
        log.info("Cron job triggered. Trying to delete all files from S3 bucket...");
        fileService.deleteAllFiles();
    }
}
