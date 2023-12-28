package com.app.testingService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.app.testingService.models.Person;

import reactor.core.publisher.Mono;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestingServiceApplicationTests {

	@Autowired
	WebTestClient wClient;
	

	@Test
	void testControllerPostPerson() {
		var p = new Person();
		p.setInfo("Info Info");
		p.setName("Ant");
		p.setNotes(null);
		wClient.post().uri("/api/persons")
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(p), Person.class)
				.exchange()
				.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(p.getName())
                .jsonPath("$.info").isEqualTo(p.getInfo());
	}

	@Test
	void testControllerPutPerson(){
		var p = new Person(null, "Ant34", "Hello", null);
		wClient.put().uri("/api/persons/3")
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(p), Person.class)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.name").isEqualTo(p.getName())
                .jsonPath("$.info").isEqualTo(p.getInfo());
	}

}
