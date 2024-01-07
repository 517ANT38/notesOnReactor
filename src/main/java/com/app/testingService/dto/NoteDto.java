package com.app.testingService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDto {
    private String title;
    private String txt;
}
