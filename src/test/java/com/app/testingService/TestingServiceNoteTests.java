package com.app.testingService;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.app.testingService.dto.AuthResultDto;
import com.app.testingService.dto.NoteDto;
import com.app.testingService.dto.PersonDtoWithNotes;
import com.app.testingService.dto.PersonLoginDto;
import com.app.testingService.models.Note;


@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class TestingServiceNoteTests {

	

	@Autowired
	private WebTestClient wClient;
		
	private String token;
	private PersonDtoWithNotes person;
	private Note testNote;

	@BeforeAll
	public void getToken(){

		var auth = PersonLoginDto.builder()
			.username("admin")
			.password("admin")
			.build();

		this.token = wClient
			.post()
			.uri("/login")
			.body(auth, PersonLoginDto.class)
			.exchange()
			.returnResult(AuthResultDto.class)
			.getResponseBody().next()
			.block().getToken();			
	}

	@BeforeAll
	public void getAdminPerson(){
		
		this.person = wClient
			.get()
			.uri("/api/persons/name/admin")
			.exchange()
			.returnResult(PersonDtoWithNotes.class)
			.getResponseBody()
			.next().blockOptional()
				.orElse(PersonDtoWithNotes.builder().id(1L)
				.username("admin")
				.enabled(true)
				.build()
			);
	}

	@BeforeAll
	public void createTestNote(){
		var note = NoteDto.builder()
			.title("test")
			.txt("This is test")
			.build();
		
		this.testNote = wClient
			.post().uri("/api/notes/person/" + person.getId())
			.header("Authorization", "Bearer " + token)
			.body(note, NoteDto.class)
			.exchange()
			.returnResult(Note.class)
			.getResponseBody()
			.next().block();	 
	}

	@Test
	void newNote() {
		var note = NoteDto.builder()
			.title("RRtest")
			.txt("This is test")
			.build();
		
		var resNote = wClient
			.post().uri("/api/notes/person/" + person.getId())
			.header("Authorization", "Bearer " + token)
			.body(note, NoteDto.class)
			.exchange()
			.expectStatus().isCreated()
			.returnResult(Note.class)
			.getResponseBody()
			.next().block();			

		assertEquals(person.getId(),resNote.getPersonId());
		assertEquals(note.getTitle(),resNote.getTitle());	
		assertEquals(note.getTxt(), resNote.getTxt());

	}

	@Test
	void getByIdAccepted(){
		wClient.get()
			.uri("/api/notes/{id}", Map.of("id", testNote.getId()))
			.exchange()
			.expectStatus().isAccepted()
			.expectBody(Note.class)
			.isEqualTo(testNote);
	}

	@Test
	void getByIdNotFound(){
		wClient.get()
			.uri("/api/notes/{id}", Map.of("id", 99))
			.exchange()
			.expectStatus().isNotFound();
	}
	
	@AfterAll
	void clearData(){
		
	

	}

}
