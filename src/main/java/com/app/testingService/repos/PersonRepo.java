package com.app.testingService.repos;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.app.testingService.models.Person;

import reactor.core.publisher.Mono;

@Repository
public interface PersonRepo extends ReactiveCrudRepository<Person,Long> {
    
    Mono<Person> findByUsername(String username);
    Mono<Person> findById(Long id);
    Mono<Boolean> existsByUsername(String username);

    @Modifying
    @Query("update person set enabled=:e where id=:id ")
    Mono<Void> updateEnablePerson(@Param("id") Long id, @Param("e") boolean e);  

    @Modifying
    @Query("update person set password=:p where id=:id ")
    Mono<Void> updatePasswordPerson(@Param("id") Long id, @Param("p") String p);  
}
