package com.app.testingService;




import java.util.Map;

import org.junit.jupiter.api.Test;
import com.app.testingService.dto.NoteDto;
import com.app.testingService.models.Note;

import reactor.core.publisher.Mono;



class TestingServiceNoteTests extends MyAbstractTestClass {		

	// save

	@Test
	void test_newNote() {
		var note = NoteDto.builder()
			.title("RRtest")
			.txt("This is test")
			.build();
		
		wClient
			.post().uri(BASE_NOTE_PATH + "/person_id/{personId}",
				Map.of("personId",person.getId()))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(note), NoteDto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.title").isEqualTo(note.getTitle())
				.jsonPath("$.txt").isEqualTo(note.getTxt());
	}
  
	// GET by id

	@Test
	void test_getById_accepted(){
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

	// Get not by noteId and personId

	@Test
	void test_getByIdAndPersonId_accepted(){
		wClient.get()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",testNote.getId(),"personId",testNote.getPersonId()))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().is2xxSuccessful()
			.expectBody(Note.class)
			.isEqualTo(testNote);
	}

	@Test
	void test_getByIdAndPersonId_notFound_noteId(){
		wClient.get()
			.uri(BASE_NOTE_PATH +"/{id}/person_id/{personId}", 
				Map.of("id", 1000,"personId",testNote.getPersonId()))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().isNotFound();
	}
	
	@Test
	void test_getByIdAndPersonId_notFound_personId(){
		wClient.get().uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",testNote.getId(),"personId", 1000))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus().isNotFound();
	}
	

	// Patch by noteId and personId

	@Test
	void test_updateNoteByNoteIdAndPersonId_accepted(){
		
		var newTitle = "Test 2";
		var newTxt = "test 2 test 2";

		var noteUpdate = NoteDto.builder()
			.title(newTitle)
			.txt(newTxt)
			.build();
		testNote.setTitle(newTitle);
		testNote.setTxt(newTxt);

		wClient.patch()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",testNote.getId(),"personId",testNote.getPersonId()))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(noteUpdate), NoteDto.class)
			.exchange()
			.expectStatus()
			.is2xxSuccessful()
			.expectBody(Note.class)
			.isEqualTo(testNote);
	}


	@Test
	void test_updateNoteByNoteIdAndPersonId_notFound_personId(){
		
		var newTitle = "Test 3";
		var newTxt = "test 3 test 3";

		var noteUpdate = NoteDto.builder()
			.title(newTitle)
			.txt(newTxt)
			.build();

		wClient.patch()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",testNote.getId(),"personId",1000))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(noteUpdate), NoteDto.class)
			.exchange()
			.expectStatus()
			.isNotFound();
	}


	@Test
	void test_updateNoteByNoteIdAndPersonId_notFound_noteId(){
		
		var newTitle = "Test 3";
		var newTxt = "test 3 test 3";

		var noteUpdate = NoteDto.builder()
			.title(newTitle)
			.txt(newTxt)
			.build();

		wClient.patch()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",1000,"personId",testNote.getPersonId()))
			.header("Authorization", "Bearer " + token)
			.body(Mono.just(noteUpdate), NoteDto.class)
			.exchange()
			.expectStatus()
			.isNotFound();
	}

	//Delete by noteId and personId	


	@Test
	void test_deleteNoteByNoteIdAndPersonId_notFound_personId(){
		
		wClient.delete()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",testNote.getId(),"personId",1000))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus()
			.isNotFound();
	}


	@Test
	void test_deleteNoteByNoteIdAndPersonId_notFound_noteId(){
		
		wClient.delete()
			.uri(BASE_NOTE_PATH + "/{id}/person_id/{personId}", 
				Map.of("id",1000,"personId",testNote.getPersonId()))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus()
			.isNotFound();
	}


	//Delete by noteId

	@Test
	void test_deleteNoteByNoteId_notFound(){
		

		wClient.delete()
			.uri(BASE_NOTE_PATH + "/{id}", 
				Map.of("id",1000))
			.header("Authorization", "Bearer " + token)
			.exchange()
			.expectStatus()
			.isNotFound();
	}



	
}
