package com.example.pw_odwsi_project.controller;

import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.UserLoginDTO;
import com.example.pw_odwsi_project.model.UserRegistrationDTO;
import com.example.pw_odwsi_project.service.UserService;
import com.example.pw_odwsi_project.util.WebUtils;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


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

    @GetMapping("/password-reset")
    public String passwordReset(@RequestParam(name = "loginError", required = false) Boolean loginError,
                        @RequestParam(name = "logoutSuccess", required = false) Boolean logoutSuccess,
                        Model model) {

        return "authentication/password-reset";
    }

}
