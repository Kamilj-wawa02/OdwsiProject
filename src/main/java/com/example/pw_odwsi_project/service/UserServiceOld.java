//package com.example.pw_odwsi_project.service;
//
//import com.example.pw_odwsi_project.domain.Note;
//import com.example.pw_odwsi_project.domain.User;
//import com.example.pw_odwsi_project.model.UserRegistrationDTO;
//import com.example.pw_odwsi_project.repos.NoteRepository;
//import com.example.pw_odwsi_project.repos.UserRepository;
//import com.example.pw_odwsi_project.util.NotFoundException;
//import com.example.pw_odwsi_project.util.WebUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.List;
//
//
//@Service
//@RequiredArgsConstructor()
//public class UserServiceOld {
//
//    private final UserRepository userRepository;
//    private final NoteRepository noteRepository;
//    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
//
//    public List<UserRegistrationDTO> findAll() {
//        final List<User> users = userRepository.findAll(Sort.by("id"));
//        return users.stream()
//                .map(user -> mapToDTO(user, new UserRegistrationDTO()))
//                .toList();
//    }
//
//    public UserRegistrationDTO get(final Long id) {
//        return userRepository.findById(id)
//                .map(user -> mapToDTO(user, new UserRegistrationDTO()))
//                .orElseThrow(NotFoundException::new);
//    }
//
//    public Long create(final UserRegistrationDTO userDTO) {
//        final User user = new User();
//        mapToEntity(userDTO, user);
//        return userRepository.save(user).getId();
//    }
//
//    public void update(final Long id, final UserRegistrationDTO userDTO) {
//        final User user = userRepository.findById(id)
//                .orElseThrow(NotFoundException::new);
//        mapToEntity(userDTO, user);
//        userRepository.save(user);
//    }
//
//    public void delete(final Long id) {
//        userRepository.deleteById(id);
//    }
//
//    private UserRegistrationDTO mapToDTO(final User user, final UserRegistrationDTO userDTO) {
//        userDTO.setId(user.getId());
//        userDTO.setUsername(user.getUsername());
//        userDTO.setEmail(user.getEmail());
//        userDTO.setPassword(user.getPassword());
//        return userDTO;
//    }
//
//    private User mapToEntity(final UserRegistrationDTO userDTO, final User user) {
//        user.setUsername(userDTO.getUsername());
//        user.setEmail(userDTO.getEmail());
//        user.setPassword(userDTO.getPassword());
//        return user;
//    }
//
//    public boolean usernameExists(final String username) {
//        return userRepository.existsByUsernameIgnoreCase(username);
//    }
//
//    public boolean emailExists(final String email) {
//        return userRepository.existsByEmailIgnoreCase(email);
//    }
//
//    public String getReferencedWarning(final Long id) {
//        final User user = userRepository.findById(id)
//                .orElseThrow(NotFoundException::new);
//        final Note userNote = noteRepository.findFirstByUser(user);
//        if (userNote != null) {
//            return WebUtils.getMessage("user.note.user.referenced", userNote.getId());
//        }
//        return null;
//    }
//
//    public static String APP_NAME = "NotesManager";
//
//
//    public String generateQRUrl(User user) throws UnsupportedEncodingException {
//        return QR_PREFIX + URLEncoder.encode(String.format(
//                        "otpauth://totp/%s:%s?secret=%s&issuer=%s",
//                        APP_NAME, user.getEmail(), user.getSecret(), APP_NAME),
//                "UTF-8");
//    }
//
//}
