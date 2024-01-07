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

    @PreAuthorize("hasRole('ADMIN')")
    public Flux<Note> findNotes(){
        return  nRepo.findAll();
    }

    
    public Flux<Note> findNotes(long personId){
        return nRepo.findByPersonId(personId);
    }

    
    public Flux<Note> findNotesByTitle(String t){
        return  nRepo.findByTitle(t)
            .switchIfEmpty(Mono.error(new NotFoundException("Note not found by title=" + t, "NOT_FOUND")));
    }

    

    
    public Mono<Note> findNoteById(long id){
        return nRepo.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Note not found by id=" + id, "NOT_FOUND")));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Note> findNoteById(long id,long personId){
               
        return nRepo.findByIdAndPersonId(id,personId)
            .switchIfEmpty(Mono.error(new NotFoundException("Note not found by id=" + id 
                + "and personId="+personId, "NOT_FOUND")));
    }

    public Mono<Note> addNewNote(Note p, long personId){
        return pRepo.existsById(personId).filter(x -> x)
        .switchIfEmpty(Mono.error(new NotFoundException("Person not found by id=" + personId, "NOT_FOUND")))
        .flatMap(x -> nRepo.save(p.toBuilder().personId(personId).build()));
    }

    
    

    public Mono<Note> updateNote(long id, long personId, Note p){
        return nRepo.existsByIdAndPersonId(id,personId).filter(x -> x)
        .switchIfEmpty(Mono.error(new NotFoundException("Note not found by id=" + id 
                    + " and personId="+personId,"NOT_FOUND")))
        .flatMap(x -> nRepo.save(p.toBuilder()
                    .id(id)
                    .personId(personId)
                    .build()));
        
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteNote(long id, long personId){
        return nRepo.existsByIdAndPersonId(id,personId).filter(x -> x)
        .switchIfEmpty(Mono.error(new NotFoundException("Note not found by id=" + id 
                    + " and personId="+personId,"NOT_FOUND")))
        .flatMap(x -> nRepo.deleteById(id));
    }

    
    public Mono<Void> deleteNote(long id){
        return nRepo.existsById(id).filter(x -> x)
        .switchIfEmpty(Mono.error(new NotFoundException("Note not found by id=" + id, "NOT_FOUND")))
        .flatMap(x -> nRepo.deleteById(id));
        
    }
    
  
}
