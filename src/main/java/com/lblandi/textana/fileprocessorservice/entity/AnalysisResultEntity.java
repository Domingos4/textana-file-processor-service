package com.lblandi.textana.fileprocessorservice.entity;

import com.lblandi.textana.fileprocessorservice.enumerated.EmotionDetectedEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalysisResultEntity {
    private String resume;
    private EmotionDetectedEnum emotionDetected;
}
