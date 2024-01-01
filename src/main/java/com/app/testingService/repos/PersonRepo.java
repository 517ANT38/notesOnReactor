package com.app.testingService.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.testingService.models.Person;

import reactor.core.publisher.Mono;

@Repository
public interface PersonRepo extends ReactiveCrudRepository<Person,Long> {
    
    Mono<Person> findByUsername(String username);
    Mono<Person> findById(Long id);
    
}
