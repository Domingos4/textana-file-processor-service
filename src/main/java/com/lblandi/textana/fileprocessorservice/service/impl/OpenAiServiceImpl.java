package com.lblandi.textana.fileprocessorservice.service.impl;

import com.lblandi.textana.fileprocessorservice.service.AiService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class OpenAiServiceImpl implements AiService {
    private static final String PROMPT_RESUME = "Analyze the following text and produce a singleparagraph summary " +
            "using concise and direct language: Text: %s. Do not include emojis, disclaimers, or metadata. Return " +
            "only the raw summary text with no additional formatting, headers, or explanations.";

    private static final String PROMPT_SENTIMENT = "Analyze the sentiment of the following text and return " +
            "exactly one word: POSITIVE | NEUTRAL | NEGATIVE. Text: %s. The output must contain only the " +
            "selected word. Do not include explanations, emojis, formatting, or any additional text.";

    private final OpenAiChatModel chatModel;

    public OpenAiServiceImpl(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String resumeText(String text) {
        return chatModel.call(PROMPT_RESUME.formatted(text));
    }

    @Override
    public String detectSentiment(String text) {
        return chatModel.call(PROMPT_SENTIMENT.formatted(text));
    }
}
