package com.app.testingService.dto;

import java.util.List;

import com.app.testingService.models.Note;

import lombok.Data;

@Data
public class PersonDtoWithNotes {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private List<Note> notes;
}
