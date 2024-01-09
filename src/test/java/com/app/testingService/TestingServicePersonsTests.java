package com.app.testingService;


import java.util.Map;

import org.junit.jupiter.api.Test;


import com.app.testingService.dto.PersonDto;

import reactor.core.publisher.Mono;

public class TestingServicePersonsTests extends MyAbstractTestClass {
    
    // add person

    @Test
	void test_newPerson() {
		var person = PersonDto.builder()
			.username("testuser")
            .password("test")
			.build();
		
        wClient
			.post().uri(BASE_PERSON_PATH + "/new")
			.body(Mono.just(person), PersonDto.class)
            .exchange()
			.expectStatus().isCreated()
			.expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.username").isEqualTo(person.getUsername())
				.jsonPath("$.enabled").isEqualTo(true);

	}

    // Get by username

    @Test
    void test_findByUsername_accepted(){
        
        wClient
            .get().uri(BASE_PERSON_PATH + "/username/{username}",
                Map.of("username", "admin"))
                .header("Authorization", "Bearer " + token)
                .exchange()                
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.username").isEqualTo(person.getUsername())
                    .jsonPath("$.enabled").isEqualTo(true);
    
    }

    @Test
    void test_findByUsername_notFound(){
        
        wClient
            .get().uri(BASE_PERSON_PATH + "/username/{username}",
                Map.of("username", "ddd"))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isNotFound();
    
    }

    // Get by id

    @Test
    void test_findById_accepted(){
        
        wClient
            .get().uri(BASE_PERSON_PATH + "/{id}",
                Map.of("id", person.getId()))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.username").isEqualTo(person.getUsername())
                    .jsonPath("$.enabled").isEqualTo(true);
    
    }

    @Test
    void test_findByid_notFound(){
        
        wClient
            .get().uri(BASE_PERSON_PATH + "/{id}",
                Map.of("id", 1000))
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus()
                .isNotFound();
    
    }

    // update person by id

    @Test
    void test_updatePersonById_accepted(){
        var newUsername = "test11";

        var p = PersonDto.builder()
            .username(newUsername)
            .firstName(testPerson.getFirstName())
            .lastName(testPerson.getLastName())
            .build();
        
        testPerson.setUsername(newUsername);
            
        wClient.patch()
            .uri(BASE_PERSON_PATH + "/{id}", Map.of("id", testPerson.getId()))
            .body(Mono.just(p), PersonDto.class)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody()
                .jsonPath("$.id").isEqualTo(testPerson.getId())
                .jsonPath("$.username").isEqualTo(newUsername)
                .jsonPath("$.firstName").isEqualTo(testPerson.getFirstName())
                .jsonPath("$.lastName").isEqualTo(testPerson.getLastName());

    }

    @Test
    void test_updatePersonById_notFound(){
        
        var newUsername = "test21";

        var p = PersonDto.builder()
            .username(newUsername)
            .firstName(testPerson.getFirstName())
            .lastName(testPerson.getLastName())
            .build();
        
            
        wClient.patch()
            .uri(BASE_PERSON_PATH + "/{id}", Map.of("id", testPerson.getId()))
            .body(Mono.just(p), PersonDto.class)
            .exchange()
            .expectStatus().isNotFound();

    }


    @Test
    void test_updatePersonById_badRequest(){
        
        var newUsername = "admin";

        var p = PersonDto.builder()
            .username(newUsername)
            .firstName(testPerson.getFirstName())
            .lastName(testPerson.getLastName())
            .build();
        
            
        wClient.patch()
            .uri(BASE_PERSON_PATH + "/{id}", Map.of("id", testPerson.getId()))
            .body(Mono.just(p), PersonDto.class)
            .exchange()
            .expectStatus().isBadRequest();

    }
}
