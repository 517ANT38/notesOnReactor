package com.app.testingService.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.MethodNotAllowedException;

import com.app.testingService.configuration.security.auth.UnauthorizedException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class AppErrorAttributes extends DefaultErrorAttributes{
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public AppErrorAttributes() {
        super();
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        var error = getError(request);

        var errorList = new ArrayList<Map<String, Object>>();

        if (error instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", ((ApiException) error).getErrorCode());
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        } else if(error instanceof NotFoundException){
            status = HttpStatus.NOT_FOUND;
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", ((ApiException) error).getErrorCode());
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        } else if (error instanceof ApiException) {
            status = HttpStatus.BAD_REQUEST;
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", ((ApiException) error).getErrorCode());
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        } else if(error instanceof MethodNotAllowedException){

            status = HttpStatus.METHOD_NOT_ALLOWED;
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code","METHOD_NOT_ALLOWED");
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);

        } else if (error instanceof ExpiredJwtException || error instanceof SignatureException || error instanceof MalformedJwtException) {
            status = HttpStatus.UNAUTHORIZED;
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", "UNAUTHORIZED");
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            var message = error.getMessage();
            if (message == null)
                message = error.getClass().getName();

            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", "INTERNAL_ERROR");
            errorMap.put("message", message);
            errorList.add(errorMap);
        }

        var errors = new HashMap<String, Object>();
        errors.put("errors", errorList);
        errorAttributes.put("status", status.value());
        errorAttributes.put("errors", errors);

        return errorAttributes;
    }

    
    public HttpStatus getStatus() {
        return status;
    }

   
    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}