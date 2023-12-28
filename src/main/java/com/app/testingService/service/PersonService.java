package com.app.testingService.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.app.testingService.models.Note;
import com.app.testingService.models.Person;
import com.app.testingService.repos.NoteRepo;
import com.app.testingService.repos.PersonRepo;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PersonService {
    
    private final PersonRepo pRepo;
    private final NoteRepo nRepo;


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
    
    public Flux<Person> findPersonsByName(String n){
        return pRepo.findByName(n);
    }

    public Mono<Person> findPersonById(int id){
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
        var v = pRepo.save(p);
        var r = v.filter(x -> x.getNotes()!=null)
                 .map(x -> x.getNotes().stream().map(y -> { 
            y.setPersonId(x.getId()); 
            nRepo.save(y);
            return y;
        }).collect(Collectors.toSet()));
        // Проверка что происходит ошибка если нет у person заметок. Исправил .filter
        // .onErrorReturn(NullPointerException.class, 
        //     new HashSet<Note>(Set.of(new Note(0,"default","default",0,null))));
        // r.subscribe(g -> System.out.println(g), e -> System.out.println(e));
        return v;
    }

    public Mono<Person> updatePerson(int id, Person p){
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
