package com.medikiosk.exception;

public class ClinicalValidationException extends RuntimeException {
    public ClinicalValidationException(String message) {
        super(message);
    }
}
