package com.app.testingService.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Note>> getNote(@PathVariable long id) {
        return nService.findNoteById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/")
    public Flux<Note> listNotes() {
        return nService.findNotes();
                
    }

    @GetMapping("/title/{title}")
    public Flux<Note> listNotesByTitle(@PathVariable String title) {
        return nService.findNotesByTitle(title);
    }

    @PostMapping
    public Mono<ResponseEntity<Note>> addNewNote(@RequestBody Note Note) {
        return nService.addNewNote(Note)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Note>> updateNote(@PathVariable long id, @RequestBody Note note) {
        return nService.updateNote(id, note)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteNote(@PathVariable long id) {
        return nService.deleteNote(id).onErrorMap(error -> error)
            .map(x -> ResponseEntity.ofNullable(x));
    }
}
