package com.app.testingService.service;

import org.springframework.stereotype.Service;

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

    public Flux<Note> findNotes(){
        return  nRepo.findAll()
            .flatMap(this::getChangeNote);
    }

    public Flux<Note> findNotesByTitle(String t){
        return  nRepo.findByTitle(t)
            .flatMap(this::getChangeNote);
    }

    public Mono<Note> findNoteById(long id){
        return nRepo.findById(id)
            .flatMap(this::getChangeNote);
    }

    public Mono<Note> addNewNote(Note p){
        return nRepo.save(p).flatMap(this::getChangeNote);
    }

    public Mono<Note> updateNote(long id, Note p){
        return nRepo.findById(id)
                .flatMap(s->{
                    p.setId(s.getId());
                    return nRepo.save(p);
                });
    }

    public Mono<Void> deleteNote(Note p){
        return nRepo.delete(p);
    }
    
    private Mono<Note> getChangeNote(Note note){
        return Mono.just(note)
                .zipWith(pRepo.findById(note.getPersonId()))
                .map(r -> {
                    r.getT1().setPerson(r.getT2());
                    return r.getT1();
                }); 
    }
}
