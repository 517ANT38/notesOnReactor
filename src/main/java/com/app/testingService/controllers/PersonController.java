package com.app.testingService.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.testingService.dto.PersonDto;
import com.app.testingService.dto.PersonDtoWithNotes;
import com.app.testingService.dto.mapper.PersonMapper;
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
    private final PersonMapper personMapper;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PersonDtoWithNotes>> getPerson(@PathVariable long id) {
        return nService.findPersonById(id)
                .map(personMapper::mapWithNotes)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Person> listPersons() {
        return nService.findPersons();
    }

   

    @PostMapping
    public Mono<ResponseEntity<PersonDtoWithNotes>> addNewPerson(@RequestBody PersonDto person) {
        return nService.addNewPerson(personMapper.map(person))
                .map(personMapper::mapWithNotes)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Person>> updatePerson(@PathVariable long id, @RequestBody PersonDto person) {
        return nService.updatePerson(id, personMapper.map(person))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePerson(@PathVariable long id) {
        return nService.deletePerson(id).onErrorMap(error -> error)
        .map(x -> ResponseEntity.ofNullable(x));
    }
}
