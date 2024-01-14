package com.example.pw_odwsi_project.controller;

import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.NoteDTO;
import com.example.pw_odwsi_project.model.NoteEditorDTO;
import com.example.pw_odwsi_project.model.NotePasswordDTO;
import com.example.pw_odwsi_project.repos.UserRepository;
import com.example.pw_odwsi_project.service.NoteService;
import com.example.pw_odwsi_project.util.WebUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/notes")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public String viewNotesList(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("notePasswordDTO", new NotePasswordDTO("123"));
        model.addAttribute("userCreatedNotes", noteService.getUserCreatedNotes(user));
        model.addAttribute("publicNotesForUser", noteService.getPublicNotesForUser(user));
        return "notes/list";
    }

    @PostMapping("/details/{id}")
    public String viewNote(@PathVariable Long id, @AuthenticationPrincipal User user, Model model, @Valid NotePasswordDTO notePasswordDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "redirect:/notes";
        }

        try {
            final NoteDTO noteDTO = noteService.getReadableNote(user, id, notePasswordDTO.password());
            model.addAttribute("noteDTO", noteDTO);
            return "notes/details";
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            return "redirect:/notes";
        }
    }

    @GetMapping("/create")
    public String createNote(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("editMode", false);
        model.addAttribute("noteEditorDTO", new NoteEditorDTO("", "", false, false, ""));
        return "notes/editor";
    }

    @PostMapping("/create")
    public String createNote(@Valid NoteEditorDTO noteEditorDTO, BindingResult bindingResult, @AuthenticationPrincipal User user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "notes/editor";
        }

        try {
            noteService.createNote(user, noteEditorDTO);
            return "redirect:/notes";
        } catch (IllegalStateException exception) {
            model.addAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            model.addAttribute("editMode", false);
            model.addAttribute("noteEditorDTO", noteEditorDTO);
            return "notes/editor";
        }
    }

    @PostMapping("/edit/{id}")
    public String editNote(@PathVariable Long id, @AuthenticationPrincipal User user, Model model, @Valid NotePasswordDTO notePasswordDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "notes/editor";
        }

        System.out.println(">>>>>>>>> requesting edit with password: '" + notePasswordDTO.password() + "' notePasswordDTO: " + notePasswordDTO);

        model.addAttribute("editMode", true);
        model.addAttribute("noteId", id);
        try {
            final NoteDTO noteDTO = noteService.getEditableNote(user, id, notePasswordDTO.password());
            model.addAttribute("noteEditorDTO", new NoteEditorDTO(noteDTO.title(), noteDTO.content(), noteDTO.isEncrypted(), noteDTO.isPublic(), notePasswordDTO.password()));
            return "notes/editor";
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            return "redirect:/notes";
        }

    }

    @PostMapping("/edit/save/{id}")
    public String editNote(@PathVariable Long id, NoteEditorDTO noteEditorDTO, BindingResult bindingResult, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "notes/editor";
        }

        try {
            noteService.updateNote(user, noteEditorDTO, id);
            return "redirect:/notes";
        } catch (IllegalStateException exception) {
            model.addAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            model.addAttribute("editMode", true);
            model.addAttribute("noteId", id);
            return "/notes/editor";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id, @AuthenticationPrincipal User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteNote(user, id);
            return "redirect:/notes";
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            return "redirect:/notes";
        }
    }

//    ----------------------------------------------------------------


//    @PostMapping("/create")
//    public String create(@ModelAttribute("note") @Valid final NoteDTO noteDTO,
//                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "notes/create";
//        }
//        noteService.create(noteDTO);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, ""); //WebUtils.getMessage("note.create.success"));
//        return "redirect:/notes";
//    }

//    @GetMapping("/edit/{id}")
//    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
//        model.addAttribute("note", noteService.get(id));
//        return "note/edit";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String edit(@PathVariable(name = "id") final Long id,
//                       @ModelAttribute("note") @Valid final NoteDTO noteDTO, final BindingResult bindingResult,
//                       final RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "note/edit";
//        }
//        noteService.update(id, noteDTO);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, ""); //WebUtils.getMessage("note.update.success"));
//        return "redirect:/notes";
//    }
//
//    @PostMapping("/delete/{id}")
//    public String delete(@PathVariable(name = "id") final Long id,
//                         final RedirectAttributes redirectAttributes) {
//        noteService.delete(id);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, ""); //WebUtils.getMessage("note.delete.success"));
//        return "redirect:/notes";
//    }

}
