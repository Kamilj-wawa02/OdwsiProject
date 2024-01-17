package com.example.pw_odwsi_project.repos;

import com.example.pw_odwsi_project.domain.Note;
import com.example.pw_odwsi_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByAuthor(User author);
    List<Note> findAllByIsPublicIsTrue();

    @Query("SELECT note FROM Note note WHERE note.id = :id AND (note.author = :user OR note.isPublic = true)")
    Optional<Note> findReadableNote(User user, Long id);

}
