package com.app.testingService;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.app.testingService.dto.AuthResultDto;
import com.app.testingService.dto.NoteDto;
import com.app.testingService.dto.PersonDtoWithNotes;
import com.app.testingService.dto.PersonLoginDto;
import com.app.testingService.models.Note;

import reactor.core.publisher.Mono;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = "spring.r2dbc.url=r2dbc:postgresql://localhost:7101/app_note_db_test" )
@TestInstance(Lifecycle.PER_CLASS)
class TestingServiceNoteTests {	

	private final static String BASE_NOTE_PATH = "/api/notes";

	@Autowired
	private WebTestClient wClient;
	@Autowired
	private ClearDB clearDB;
	
	private String token;
	private PersonDtoWithNotes person;
	private Note testNote;

	@BeforeAll
	public void getToken(){

		this.token = requestToken("admin","admin");
		this.person = requestAdminPerson();
		var note = NoteDto.builder()
			.title("test")
			.txt("This is test")
			.build();
		this.testNote = createTestNote(token,note);
	}	

	@Test
	void test_newNote() {
		var note = NoteDto.builder()
			.title("RRtest")
			.txt("This is test")
			.build();
		
		var resNote = createTestNote(token, note);			

		assertEquals(person.getId(),resNote.getPersonId());
		assertEquals(note.getTitle(),resNote.getTitle());	
		assertEquals(note.getTxt(), resNote.getTxt());

	}

	@Test
	void test_getByIdAccepted(){
		wClient.get()
			.uri(BASE_NOTE_PATH + "/{id}", Map.of("id", testNote.getId()))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectBody(Note.class)
			.isEqualTo(testNote);
	}

	@Test
	void test_getById_notFound(){
		wClient.get()
			.uri(BASE_NOTE_PATH +"/{id}", Map.of("id", 1000))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().isNotFound();
	}
	
	@Test
	void test_updateNoteByNoteIdAndPersonId_accepted(){
		
		var newTitle = "Test 2";
		var newTxt = "test 2 test 2";

		var noteUpdate = NoteDto.builder()
			.title(newTitle)
			.txt(newTxt)
			.build();
		testNote = testNote.toBuilder()
			.title(newTitle)
			.txt(newTxt)
			.build();

		wClient.patch()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", Map.of("id",testNote.getId(),
				"personId",testNote.getPersonId()))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(noteUpdate), NoteDto.class)
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody(Note.class)
			.isEqualTo(testNote);
	}


	@Test
	void test_updateNoteByNoteIdAndPersonId_notFound(){
		
		var newTitle = "Test 3";
		var newTxt = "test 3 test 3";

		var noteUpdate = NoteDto.builder()
			.title(newTitle)
			.txt(newTxt)
			.build();

		wClient.patch()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", Map.of("id",testNote.getId(),
				"personId",1000))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(noteUpdate), NoteDto.class)
			.exchange()
			.expectStatus()
			.isNotFound();
	}


	@Test
	void test_updateNoteByNoteId_notFound(){
		
		var newTitle = "Test 3";
		var newTxt = "test 3 test 3";

		var noteUpdate = NoteDto.builder()
			.title(newTitle)
			.txt(newTxt)
			.build();

		wClient.patch()
			.uri(BASE_NOTE_PATH + "/{id}", Map.of("id",1000))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(noteUpdate), NoteDto.class)
			.exchange()
			.expectStatus()
			.isNotFound();
	}


	@AfterAll
	void clearData(){
		
		clearDB.clear();

	}


	private String requestToken(String username, String password){

		var auth = PersonLoginDto.builder()
			.username("admin")
			.password("admin")
			.build();
		return wClient
			.post()
			.uri("/login")
			.body(Mono.just(auth), PersonLoginDto.class)
			.exchange()
			.returnResult(AuthResultDto.class)
			.getResponseBody().next()
			.block().getToken();	
	}

	private PersonDtoWithNotes requestAdminPerson(){
		
		return wClient
			.get()
			.uri("/api/persons/{username}",Map.of("username","admin"))
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

	private Note createTestNote(String token, NoteDto note){
		
		
		return wClient
			.post().uri(BASE_NOTE_PATH + "/person_id/{personId}",
				Map.of("personId",person.getId()))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(note), NoteDto.class)
			.exchange()
			.returnResult(Note.class)
			.getResponseBody()
			.next().block();	 
	}
}
