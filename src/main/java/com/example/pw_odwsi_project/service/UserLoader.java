package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.domain.Note;
import com.example.pw_odwsi_project.repos.NoteRepository;
import com.example.pw_odwsi_project.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Override
    public void run(final ApplicationArguments args) {
//        if (userRepository.count() != 0) {
//            return;
//        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> Creating user, current count: " + userRepository.count());
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("testtest"));
        user.setSecret("");
        userRepository.save(user);

        Note note = new Note();
        note.setTitle("test_note");
        note.setContent("<h1>AAA</h1>\n<p>test_content</p>");
        note.setIsPublic(false);
        note.setIsEncrypted(false);
        note.setAuthor(user);
        noteRepository.save(note);

        note = new Note();
        note.setTitle("test_note");
        note.setContent("<h1>AAA</h1>\n<p>test_content</p>");
        note.setIsPublic(true);
        note.setIsEncrypted(false);
        note.setAuthor(user);
        noteRepository.save(note);

        note = new Note();
        note.setTitle("test_note-Encrypted");
        note.setContent(encryptionService.encrypt("<h1>AAA</h1>\n<p>test_content Encrypted</p>", "123"));
        note.setIsPublic(false);
        note.setIsEncrypted(true);
        note.setAuthor(user);
        noteRepository.save(note);

        user = new User();
        user.setUsername("testt");
        user.setEmail("testt@testt.com");
        user.setPassword(passwordEncoder.encode("testtest"));
        user.setSecret("");
        userRepository.save(user);

        note = new Note();
        note.setTitle("test_note-2");
        note.setContent("<h1>AAA2</h1>\n<p>test_content2</p>");
        note.setIsPublic(true);
        note.setIsEncrypted(false);
        note.setAuthor(user);
        noteRepository.save(note);
    }

}
