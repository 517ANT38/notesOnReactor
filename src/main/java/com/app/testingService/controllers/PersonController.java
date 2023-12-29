package com.app.testingService.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.testingService.models.Person;
import com.app.testingService.service.PersonService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/persons")
@AllArgsConstructor
public class PersonController {
    
    private final PersonService nService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Person>> getPerson(@PathVariable int id) {
        return nService.findPersonById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/")
    public Flux<Person> listPersons() {
        return nService.findPersons();
    }

    @GetMapping("/name/{name}")
    public Flux<Person> listPersonsByName(@PathVariable String name) {
        return nService.findPersonsByName(name);
    }

    @PostMapping
    public Mono<ResponseEntity<Person>> addNewPerson(@RequestBody Person Person) {
        return nService.addNewPerson(Person)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Person>> updatePerson(@PathVariable int id, @RequestBody Person Person) {
        return nService.updatePerson(id, Person)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Void>> deletePerson(@PathVariable int id) {
        return nService.findPersonById(id)
                .flatMap(s ->
                        nService.deletePerson(s)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
