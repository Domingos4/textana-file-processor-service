package com.lblandi.textana.fileprocessorservice.exception;

import java.io.Serial;

public class FileProcessingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileProcessingException(String fileIdentifier) {
        super("An error occurred while trying to process file with identifier: " + fileIdentifier);
    }
}
