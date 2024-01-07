package com.app.testingService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonLoginDto {
    private String username;
    private String password;
}
