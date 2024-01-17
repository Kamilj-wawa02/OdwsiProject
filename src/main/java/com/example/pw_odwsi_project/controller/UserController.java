package com.example.pw_odwsi_project.controller;

import com.example.pw_odwsi_project.auth.CustomAuthenticationProvider;
import com.example.pw_odwsi_project.domain.User;
import com.example.pw_odwsi_project.model.UserPasswordChangeDTO;
import com.example.pw_odwsi_project.model.UserPasswordResetDTO;
import com.example.pw_odwsi_project.model.UserRegistrationDTO;
import com.example.pw_odwsi_project.service.BindingErrorHandler;
import com.example.pw_odwsi_project.service.UserService;
import com.example.pw_odwsi_project.util.WebUtils;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController {

    private final UserService userService;
    private  final BindingErrorHandler bindingErrorHandler;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO("", "", "", "", ""));
        return "authentication/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingErrorHandler.handleBindingError(bindingResult, model);
            return "authentication/register";
        }

        if (userRegistrationDTO.veryNecessaryCode() != null) {
            log.warn("Honeypot detected a bot trying to fill in fake field with '" + userRegistrationDTO.veryNecessaryCode() +
                    "'! More information: username=" + userRegistrationDTO.username() + ", email=" + userRegistrationDTO.email());
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
                        HttpServletRequest request,
                        Model model) {
        if (loginError == Boolean.TRUE) {
            AuthenticationException authenticationException = (AuthenticationException) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (authenticationException != null && authenticationException.getMessage().equals(CustomAuthenticationProvider.MAX_LOGIN_ATTEMPTS_MSG)) {
                model.addAttribute(WebUtils.MSG_ERROR, CustomAuthenticationProvider.MAX_LOGIN_ATTEMPTS_MSG);
            } else {
                model.addAttribute(WebUtils.MSG_ERROR, "Your login was not successful - please try again");
            }
        } else if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute(WebUtils.MSG_SUCCESS, "Successfully logged out");
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
            bindingErrorHandler.handleBindingError(bindingResult, model);
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
        if (!userService.validatePasswordResetToken(token)) {
            System.out.println("-> invalid token: " + token);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, UserService.INVALID_TOKEN_MSG);
            return "redirect:/login";
        }
        System.out.println("-> VALID token: " + token);

        model.addAttribute("userPasswordChangeDTO", new UserPasswordChangeDTO("", "", token));
        return "authentication/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid UserPasswordChangeDTO userPasswordChangeDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingErrorHandler.handleBindingError(bindingResult, model);
            return "authentication/change-password";
        }

        System.out.println("performing password reset on: " + userPasswordChangeDTO.token());

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
