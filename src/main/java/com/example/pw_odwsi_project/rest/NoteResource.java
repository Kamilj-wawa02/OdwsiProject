//package com.example.pw_odwsi_project.rest;
//
//import com.example.pw_odwsi_project.model.NoteDTO;
//import com.example.pw_odwsi_project.service.NoteService;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//
//@RestController
//@RequestMapping(value = "/api/notes", produces = MediaType.APPLICATION_JSON_VALUE)
//public class NoteResource {
//
//    private final NoteService noteService;
//
//    public NoteResource(final NoteService noteService) {
//        this.noteService = noteService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<NoteDTO>> getAllNotes() {
//        return ResponseEntity.ok(noteService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<NoteDTO> getNote(@PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(noteService.get(id));
//    }
//
//    @PostMapping
//    @ApiResponse(responseCode = "201")
//    public ResponseEntity<Long> createNote(@RequestBody @Valid final NoteDTO noteDTO) {
//        final Long createdId = noteService.create(noteDTO);
//        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Long> updateNote(@PathVariable(name = "id") final Long id,
//            @RequestBody @Valid final NoteDTO noteDTO) {
//        noteService.update(id, noteDTO);
//        return ResponseEntity.ok(id);
//    }
//
//    @DeleteMapping("/{id}")
//    @ApiResponse(responseCode = "204")
//    public ResponseEntity<Void> deleteNote(@PathVariable(name = "id") final Long id) {
//        noteService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//}
