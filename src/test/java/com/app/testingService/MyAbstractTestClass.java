package com.app.testingService;


import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.app.testingService.dto.AuthResultDto;
import com.app.testingService.dto.NoteDto;
import com.app.testingService.dto.PersonDto;
import com.app.testingService.dto.PersonDtoWithNotes;
import com.app.testingService.dto.PersonLoginDto;
import com.app.testingService.models.Note;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = "spring.r2dbc.url=r2dbc:postgresql://localhost:7101/app_note_db_test" )
@TestInstance(Lifecycle.PER_CLASS)
public abstract class MyAbstractTestClass {
    
    protected final static String BASE_NOTE_PATH = "/api/notes";
    protected static final String BASE_PERSON_PATH = "/api/persons";

	@Autowired
	protected  WebTestClient wClient;
	@Autowired
	protected ClearDB clearDB;
	
	protected String token;
	protected PersonDtoWithNotes testPerson;
    protected PersonDtoWithNotes person;
	protected Note testNote;

    @BeforeAll
	public void getToken(){

		this.token = requestToken("admin","admin");
		this.person = requestAdminPerson(this.token);
		var note = NoteDto.builder()
			.title("test")
			.txt("This is test")
			.build();
		this.testNote = createTestNote(this.token,note,this.person);
        var person = PersonDto.builder()
			.username("tester")
            .password("test")
			.firstName("BBB")
			.lastName("YYY")
			.build();
        this.testPerson = createTestPeron(person);
		System.out.println(testPerson);
	}	

    @AfterAll
	public void clearData(){
		
		clearDB.clear();

	}

    private  PersonDtoWithNotes createTestPeron(PersonDto dto){
       
		
        return wClient
			.post().uri(BASE_PERSON_PATH + "/new")
			.body(Mono.just(dto), PersonDto.class)
			.exchange()
			.expectBody(PersonDtoWithNotes.class)
			.returnResult()
            .getResponseBody();
            
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

	private PersonDtoWithNotes requestAdminPerson(String token){
		
		var p = wClient
			.get()
			.uri("/api/persons/username/{username}",Map.of("username","admin"))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectBody(PersonDtoWithNotes.class)
            .returnResult()
            .getResponseBody();
		return p;
	}

	private Note createTestNote(String token, NoteDto note, PersonDtoWithNotes person){
		
		
		return wClient
			.post().uri(BASE_NOTE_PATH + "/person_id/{personId}",
				Map.of("personId",person.getId()))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(note), NoteDto.class)
			.exchange()
			.expectBody(Note.class)
            .returnResult()
			.getResponseBody();	 
	}
}
