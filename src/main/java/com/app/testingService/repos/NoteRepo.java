package com.app.testingService.repos;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.testingService.models.Note;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NoteRepo extends ReactiveCrudRepository<Note, Long> {
    @Query("select * from note n where n.title=:t")
    Flux<Note> findByTitle(@Param("t") String t);
    Mono<Note> findByIdAndPersonId(Long id, Long personId);
    Flux<Note> findByPersonId(Long id);
    Mono<Boolean> existsByIdAndPersonId(Long id, Long personId);
    
}
