package com.app.testingService;

import org.springframework.stereotype.Component;

import com.app.testingService.repos.NoteRepo;
import com.app.testingService.repos.PersonRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClearDB {
    private final PersonRepo personRepo;
    private final NoteRepo noteRepo;

    void clear(){
        noteRepo.deleteAll().block();
        personRepo.deleteAll().block();
    }
}
