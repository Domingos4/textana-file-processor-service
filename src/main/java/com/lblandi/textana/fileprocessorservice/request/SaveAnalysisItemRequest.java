package com.lblandi.textana.fileprocessorservice.request;

import com.lblandi.textana.fileprocessorservice.enumerated.FileAnalysisStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SaveAnalysisItemRequest {
    private String fileIdentifier;
    private FileAnalysisStatusEnum status;
    private LocalDateTime lastStepAt;
}
