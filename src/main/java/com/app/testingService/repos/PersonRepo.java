package com.app.testingService.repos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.testingService.models.Person;

import reactor.core.publisher.Flux;

@Repository
public interface PersonRepo extends ReactiveCrudRepository<Person,Integer> {
    
    Flux<Person> findByName(String n);
}
