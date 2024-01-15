package com.example.pw_odwsi_project.controller;

import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.UserPasswordChangeDTO;
import com.example.pw_odwsi_project.model.UserPasswordResetDTO;
import com.example.pw_odwsi_project.model.UserRegistrationDTO;
import com.example.pw_odwsi_project.service.UserService;
import com.example.pw_odwsi_project.util.WebUtils;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


@Controller
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO("", "", "", ""));
        return "authentication/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "authentication/register";
        }

        try {
            User user = userService.createUser(userRegistrationDTO);
            String qrUrl = userService.generateUserQRUrl(user);
            model.addAttribute("qrUrl", qrUrl);
            return "authentication/register-qr";
        } catch (IllegalStateException | IOException | WriterException exception) {
            model.addAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            return "authentication/register";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "loginError", required = false) Boolean loginError,
                        @RequestParam(name = "logoutSuccess", required = false) Boolean logoutSuccess,
                        Model model) {
        if (loginError == Boolean.TRUE) {

            model.addAttribute(WebUtils.MSG_ERROR, "Your login was not successful - please try again");
        } else if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_INFO, "Successfully logged out");
        }
        //model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "authentication/login";
    }

    @GetMapping("/reset-password")
    public String passwordReset(Model model) {
        model.addAttribute("userPasswordResetDTO", new UserPasswordResetDTO(""));
        return "authentication/reset-password";
    }

    @PostMapping("/reset-password")
    public String passwordReset(@Valid UserPasswordResetDTO userPasswordResetDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "authentication/reset-password";
        }

        try {
            userService.sendResetPasswordEmail(userPasswordResetDTO.email());
            model.addAttribute(WebUtils.MSG_SUCCESS, "Successfully sent email with password reset link to "
                    + userPasswordResetDTO.email());
        } catch (Exception exception) {
            model.addAttribute(WebUtils.MSG_ERROR, exception.getMessage());
        }

        model.addAttribute("userPasswordResetDTO", userPasswordResetDTO);
        return "authentication/reset-password";
    }

    @GetMapping("/change-password")
    public String changePassword(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        if (!userService.validateResetPasswordToken(token)) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Invalid token.");
            return "redirect:/login";
        }

        model.addAttribute("userPasswordChangeDTO", new UserPasswordChangeDTO("", "", token));
        return "authentication/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid UserPasswordChangeDTO userPasswordChangeDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, bindingResult.getAllErrors().toString());
            return "authentication/change-password";
        }

        try {
            userService.changePassword(userPasswordChangeDTO);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Successfully changed password.");
            return "redirect:/login";
        } catch (IllegalStateException exception) {
            model.addAttribute(WebUtils.MSG_ERROR, exception.getMessage());
            return "authentication/change-password";
        }
    }


}
