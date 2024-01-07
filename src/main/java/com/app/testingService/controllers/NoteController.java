package com.app.testingService.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.testingService.dto.NoteDto;
import com.app.testingService.dto.mapper.NoteMapper;
import com.app.testingService.models.Note;
import com.app.testingService.service.NoteService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notes")
@AllArgsConstructor
public class NoteController {
    
    private final NoteService nService;
    private final NoteMapper mNoteMapper;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Note>> getNote(@PathVariable long id) {
        return nService.findNoteById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}/person_id/{personId}")
    public Mono<ResponseEntity<Note>> getNote(@PathVariable("id") long id,
        @PathVariable("personId") long personId) {
        return nService.findNoteById(id,personId)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Note> listNotes() {
        return nService.findNotes();
                
    }

    @GetMapping("/person_id/{personId}")
    public Flux<Note> listNotes(@PathVariable("personId") long personId) {
        return nService.findNotes(personId);
                
    }

    @GetMapping("/title/{title}/")
    public Flux<Note> listNotesByTitle(@PathVariable String title) {
        return nService.findNotesByTitle(title);
    }

    @GetMapping("/title/{title}/person_id/{personId}")
    public Flux<Note> listNotesByTitle(@PathVariable("title") String title,
        @PathVariable("personId") long personId) {
        return nService.findNotesByTitle(title,personId);
    }

    @PostMapping("/person_id/{personId}")
    public Mono<ResponseEntity<Note>> addNewNote(@PathVariable("personId") long personId, @RequestBody NoteDto dto){
        return nService.addNewNote(mNoteMapper.map(dto),personId)
                .map(x -> ResponseEntity.status(HttpStatus.CREATED).body(x));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Note>> updateNote(@PathVariable long id, @RequestBody NoteDto note) {
        return nService.updateNote(id, mNoteMapper.map(note))
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/person_id/{personId}")
    public Mono<ResponseEntity<Note>> updateNote(@PathVariable("id") long id, 
        @PathVariable("personId") long personId, @RequestBody NoteDto note) {
        return nService.updateNote(id,personId,mNoteMapper.map(note))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteNote(@PathVariable long id) {
        return nService.deleteNote(id).onErrorMap(error -> error)
            .map(x -> ResponseEntity.ofNullable(x));
    }


    @DeleteMapping("/{id}/person_id/{personId}")
    public Mono<ResponseEntity<Void>> deleteNote(@PathVariable("id") long id, 
        @PathVariable("personId") long personId){
        return nService.deleteNote(id,personId).onErrorMap(error -> error)
            .map(x -> ResponseEntity.ofNullable(x));
    }
}
