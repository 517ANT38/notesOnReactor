package com.app.testingService.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.testingService.app.NotFoundException;
import com.app.testingService.models.Person;
import com.app.testingService.repos.NoteRepo;
import com.app.testingService.repos.PersonRepo;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    void createDefaultAdmin(){
        var admin = pRepo.findByUsername("admin")
            .switchIfEmpty(pRepo.save(Person.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .enabled(true) 
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .createdAt(LocalDateTime.now())
                .build()))
            .block();
            log.info("Created admin with ID = " + admin.getId());
    }

    @PreAuthorize("hasRole('Admin')")
    public Flux<Person> findPersons(){
        return pRepo.findAll()
               .flatMap(x -> {
                    return nRepo.findByPersonId(x.getId())
                        .collect(Collectors.toSet()).map(y -> x.toBuilder().notes(y).build());
                });
                
    }
    
    @PreAuthorize("hasRole('Admin')")
    public Mono<Person> findPersonsByUserName(String n){
        return pRepo.findByUsername(n);
    }

    @PreAuthorize("hasRole('Admin')")
    public Mono<Person> findPersonById(long id){
        return nRepo.findByPersonId(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Person not found by id=" + id, "NOT_FOUND")))
            .collect(Collectors.toSet())
            .flatMap(p -> {
                return pRepo.findById(id).map(x -> x.toBuilder()
                    .notes(p)
                    .build());
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
        return pRepo.existsById(id).flatMap(x -> {
            if (x) {
                return pRepo.save(p.toBuilder().id(id).build());
            } else {
                return Mono.error(new NotFoundException("Person not found by id=" + id, "NOT_FOUND"));
            }
        });
       
    }

    @PreAuthorize("hasRole('Admin')")
    public Mono<Void> deletePerson(long id){
        return  pRepo.existsById(id).flatMap(x -> {
            if (x) {
                return pRepo.deleteById(id);
            } else {
                return Mono.error(new NotFoundException("Person not found by id=" + id, "NOT_FOUND"));
            }
        });
        
    }
}
