package com.app.testingService.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.testingService.models.Person;
import com.app.testingService.repos.NoteRepo;
import com.app.testingService.repos.PersonRepo;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class PersonService {
    
    private final PersonRepo pRepo;
    private final NoteRepo nRepo;
    private final PasswordEncoder passwordEncoder;

    public Flux<Person> findPersons(){
        return pRepo.findAll()
               .flatMap(x -> {
                    return nRepo.findByPersonId(x.getId())
                        .collect(Collectors.toSet()).map(y -> {
                                x.setNotes(y);
                                    return x;
                        });
                    
                });
                
    }
    
    public Mono<Person> findPersonsByUserName(String n){
        return pRepo.findByUsername(n);
    }

    public Mono<Person> findPersonById(long id){
        return nRepo.findByPersonId(id)
            .collect(Collectors.toSet())
            .flatMap(p -> {
                return pRepo.findById(id).map(x ->{
                    x.setNotes(p);
                    return x;
                });
            });
    }

    public Mono<Person> addNewPerson(Person p){
        return pRepo.save(p.toBuilder().password(passwordEncoder.encode(p.getPassword()))
            .roles(Collections.singletonList("ROLE_USER"))
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .build())
            .doOnSuccess(u -> log.info("Created new user with ID = " + u.getId()));
    }

    public Mono<Person> updatePerson(long id, Person p){
        return pRepo.findById(id)
                .flatMap(s->{
                    p.setId(s.getId());
                    return pRepo.save(p);
                });
    }

    public Mono<Void> deletePerson(Person p){
        return pRepo.delete(p);
    }
}
