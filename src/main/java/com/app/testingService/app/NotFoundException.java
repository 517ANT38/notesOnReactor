package com.app.testingService.app;

public class NotFoundException extends ApiException {

    public NotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
    
}
