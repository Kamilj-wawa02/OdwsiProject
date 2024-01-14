//package com.example.pw_odwsi_project.rest;
//
//import com.example.pw_odwsi_project.model.UserRegistrationDTO;
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
//@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
//public class UserResource {
//
//    private final UserServiceOld userService;
//
//    public UserResource(final UserServiceOld userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<UserRegistrationDTO>> getAllUsers() {
//        return ResponseEntity.ok(userService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserRegistrationDTO> getUser(@PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(userService.get(id));
//    }
//
//    @PostMapping
//    @ApiResponse(responseCode = "201")
//    public ResponseEntity<Long> createUser(@RequestBody @Valid final UserRegistrationDTO userDTO) {
//        final Long createdId = userService.create(userDTO);
//        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Long> updateUser(@PathVariable(name = "id") final Long id,
//            @RequestBody @Valid final UserRegistrationDTO userDTO) {
//        userService.update(id, userDTO);
//        return ResponseEntity.ok(id);
//    }
//
//    @DeleteMapping("/{id}")
//    @ApiResponse(responseCode = "204")
//    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final Long id) {
//        userService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//}
