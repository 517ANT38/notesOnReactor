package com.app.testingService.service.security;

import com.app.testingService.app.ApiException;

public class AuthException extends ApiException{

    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
    
}
