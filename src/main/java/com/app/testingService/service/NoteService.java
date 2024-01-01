package com.app.testingService.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.app.testingService.app.NotFoundException;
import com.app.testingService.models.Note;
import com.app.testingService.repos.NoteRepo;
import com.app.testingService.repos.PersonRepo;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class NoteService {

    private final NoteRepo nRepo;
    private final PersonRepo pRepo;

    // @PreAuthorize("hasRole('ADMIN')")
    public Flux<Note> findNotes(){
        return  nRepo.findAll()
            .flatMap(this::getChangeNote);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public Flux<Note> findNotesByTitle(String t){
        return  nRepo.findByTitle(t)
            .flatMap(this::getChangeNote)
            .switchIfEmpty(Mono.error(new NotFoundException("Note not found by title=" + t, "NOT_FOUND")));
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public Mono<Note> findNoteById(long id){
        return nRepo.findById(id)
            .flatMap(this::getChangeNote)
            .switchIfEmpty(Mono.error(new NotFoundException("Note not found by id=" + id, "NOT_FOUND")));
    }

    public Mono<Note> addNewNote(Note p){
        return nRepo.save(p).flatMap(this::getChangeNote);
    }

    public Mono<Note> updateNote(long id, Note p){
        return nRepo.existsById(id).flatMap(x -> {
            if (x) {
                return nRepo.save(p.toBuilder().id(id).build());
            } else {
                return Mono.error(new NotFoundException("Note not found by id=" + id, "NOT_FOUND"));
            }
        });
    }

    public Mono<Void> deleteNote(long id){
        return nRepo.existsById(id).flatMap(x -> {
            if (x) {
                return nRepo.deleteById(id);
            } else {
                return Mono.error(new NotFoundException("Note not found by id=" + id, "NOT_FOUND"));
            }
        });
        
    }
    
    private Mono<Note> getChangeNote(Note note){
        return Mono.just(note)
                .zipWith(pRepo.findById(note.getId()))
                .map(r -> {
                    r.getT1().setPerson(r.getT2());
                    return r.getT1();
                }); 
    }
}
