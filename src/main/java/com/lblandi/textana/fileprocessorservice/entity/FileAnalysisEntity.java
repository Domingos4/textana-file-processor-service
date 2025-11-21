package com.lblandi.textana.fileprocessorservice.entity;

import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileAnalysisEntity {
    private String uuid;
    private FileAnalysisStatusEnum status;
    private AnalysisResultEntity result;
    private LocalDateTime lastStepAt;
}
