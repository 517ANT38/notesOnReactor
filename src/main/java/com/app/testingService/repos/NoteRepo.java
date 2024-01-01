package com.app.testingService.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.testingService.models.Note;

import reactor.core.publisher.Flux;

@Repository
public interface NoteRepo extends ReactiveCrudRepository<Note, Long> {
    
    Flux<Note> findByTitle(String t);

    Flux<Note> findByPersonId(Long id);
}
