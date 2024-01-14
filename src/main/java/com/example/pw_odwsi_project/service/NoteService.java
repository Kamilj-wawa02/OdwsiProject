package com.example.pw_odwsi_project.service;

import com.example.pw_odwsi_project.domain.Note;
import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.NoteDTO;
import com.example.pw_odwsi_project.model.NoteEditorDTO;
import com.example.pw_odwsi_project.repos.NoteRepository;
import com.example.pw_odwsi_project.util.NotFoundException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.owasp.html.Sanitizers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final EncryptionService encryptionService;
    private static final String NOTE_PUBLIC_AND_ENCRYPTED_MESSAGE = "A note cannot be both public and encrypted.";
    private static final String USER_NOT_AUTHOR = "Only note's author can perform this action.";
    private static final String PASSWORD_REQUIRED_FOR_ENCRYPTED_NOTE = "Note is encrypted and password is required.";

    @Transactional(readOnly = true)
    public NoteDTO getReadableNote(@NotNull User user, @NotNull Long id, @Nullable String password) {
        final Note note = noteRepository.findReadableNote(user, id).orElseThrow(NotFoundException::new);
        if (note.getIsEncrypted()) {
            if (password == null || password.isBlank()) {
                throw new IllegalStateException(PASSWORD_REQUIRED_FOR_ENCRYPTED_NOTE);
            }
            note.setContent(decryptNote(note.getContent(), password));
        }
        return mapNoteToNoteDTO(note);
    }

    @Transactional(readOnly = true)
    public NoteDTO getEditableNote(@NotNull User author, @NotNull Long id, @Nullable String password) {
        final Note note = noteRepository.findById(id).orElseThrow(NotFoundException::new);
        validateUserNoteAuthor(note, author);
        if (note.getIsEncrypted()) {
            if (password == null || password.isBlank()) {
                throw new IllegalStateException(PASSWORD_REQUIRED_FOR_ENCRYPTED_NOTE);
            }
            note.setContent(decryptNote(note.getContent(), password));
        }
        return mapNoteToNoteDTO(note);
    }

    @Transactional(readOnly = true)
    public List<NoteDTO> getUserCreatedNotes(User author) {
        return noteRepository.findAllByAuthor(author).stream().map(this::mapNoteToNoteDTO)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    List<NoteDTO> reversed = new ArrayList<>(list);
                    Collections.reverse(reversed);
                    return reversed;
                }));
    }

    @Transactional(readOnly = true)
    public List<NoteDTO> getPublicNotesForUser(User author) {
        return noteRepository.findAllByIsPublicIsTrue().stream()
                .filter(note -> !note.getAuthor().getUsername().equals(author.getUsername()))
                .map(this::mapNoteToNoteDTO).collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    List<NoteDTO> reversed = new ArrayList<>(list);
                    Collections.reverse(reversed);
                    return reversed;
                }));
    }

    @Transactional
    public NoteDTO createNote(@NotNull User author, @NotNull NoteEditorDTO noteEditorDTO) {
        if (noteEditorDTO.isPublic() && noteEditorDTO.isEncrypted()) {
            throw new IllegalStateException(NOTE_PUBLIC_AND_ENCRYPTED_MESSAGE);
        }

        final String sanitizedContent = sanitizeContentInHtml(noteEditorDTO.content());
        final String finalContent = noteEditorDTO.isEncrypted() ?
                encryptNote(sanitizedContent, noteEditorDTO.password()) : sanitizedContent;
        final Note note = new Note(author, noteEditorDTO.title(), finalContent, noteEditorDTO.isPublic(),
                noteEditorDTO.isEncrypted());
        noteRepository.save(note);

        return mapNoteToNoteDTO(note);
    }

    @Transactional
    public NoteDTO updateNote(@NotNull User author, @NotNull NoteEditorDTO noteEditorDTO, @NotNull Long id) {
        if (noteEditorDTO.isEncrypted() && noteEditorDTO.isPublic()) {
            throw new IllegalStateException(NOTE_PUBLIC_AND_ENCRYPTED_MESSAGE);
        }

        final Note note = noteRepository.findById(id).orElseThrow(NotFoundException::new);
        validateUserNoteAuthor(note, author);

        final String sanitizedContent = sanitizeContentInHtml(noteEditorDTO.content());
        final String finalContent = noteEditorDTO.isEncrypted() ?
                encryptNote(sanitizedContent, noteEditorDTO.password()) : sanitizedContent;

        note.setTitle(noteEditorDTO.title());
        note.setContent(finalContent);
        note.setIsPublic(noteEditorDTO.isPublic());
        note.setIsEncrypted(noteEditorDTO.isEncrypted());
        noteRepository.save(note);

        return mapNoteToNoteDTO(note);
    }

    @Transactional
    public void deleteNote(@NotNull User author, @NotNull Long id) {
        final Note note = noteRepository.findById(id).orElseThrow(NotFoundException::new);
        validateUserNoteAuthor(note, author);
        noteRepository.delete(note);
    }

    private String sanitizeContentInHtml(String content) {
        return Sanitizers.FORMATTING
                .and(Sanitizers.LINKS).and(Sanitizers.IMAGES).and(Sanitizers.BLOCKS)
                .sanitize(content);
    }

    private void validateUserNoteAuthor(Note note, User user) {
        if (!user.getUsername().equals(note.getAuthor().getUsername())) {
            throw new IllegalStateException(USER_NOT_AUTHOR);
        }
    }

    private String encryptNote(String content, String password) {
        return encryptionService.encrypt(content, password);
    }

    private String decryptNote(String content, String password) {
        return encryptionService.decrypt(content, password);
    }

    private NoteDTO mapNoteToNoteDTO(final Note note) {
        return new NoteDTO(
                note.getId(), note.getTitle(), note.getContent(),
                note.getAuthor().getUsername(), note.getIsEncrypted(),
                note.getIsPublic());
    }

}
