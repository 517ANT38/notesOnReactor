package com.app.testingService.dto;

import java.util.List;

import com.app.testingService.models.Note;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDtoWithNotes {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private List<Note> notes;
}
