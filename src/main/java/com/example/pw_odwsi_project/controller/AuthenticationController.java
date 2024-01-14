//package com.example.pw_odwsi_project.controller;
//
//import com.example.pw_odwsi_project.model.UserRegistrationDTO;
//import com.example.pw_odwsi_project.service.UserServiceOld;
//import com.example.pw_odwsi_project.util.WebUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequiredArgsConstructor
//public class AuthenticationController {
//
//    private final UserServiceOld userService;
//
//    private final PasswordEncoder passwordEncoder;
//
//    @GetMapping("/register")
//    public String register(@RequestParam(name = "registerError", required = false) final Boolean registerError,
//                        final Model model) {
//        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
//        if (registerError == Boolean.TRUE) {
//            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.register.error"));
//        }
//        return "authentication/register";
//    }
//
//    @PostMapping("/register")
//    public String registerUser(@ModelAttribute("userRegistrationDTO") @Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult, final Model model) {
//        if (!bindingResult.hasFieldErrors("username") && userService.usernameExists(userRegistrationDTO.getUsername())) {
//            bindingResult.rejectValue("username", "Exists.user.username");
//            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("Exists.user.username"));
//        } else if (!bindingResult.hasFieldErrors("email") && userService.emailExists(userRegistrationDTO.getEmail())) {
//            bindingResult.rejectValue("email", "Exists.user.email");
//            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("Exists.user.email"));
//        }
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("userRegistrationDTO", userRegistrationDTO);
//            return "authentication/register";
//        }
//
//        userRegistrationDTO.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
//        userService.create(userRegistrationDTO);
//        return "redirect:/login?registrationSuccess=true";
//    }
//
//    @GetMapping("/login")
//    public String login(@RequestParam(name = "loginRequired", required = false) final Boolean loginRequired,
//                        @RequestParam(name = "loginError", required = false) final Boolean loginError,
//                        @RequestParam(name = "logoutSuccess", required = false) final Boolean logoutSuccess,
//                        final Model model) {
//        if (loginRequired == Boolean.TRUE) {
//            model.addAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("authentication.login.required"));
//        }
//        if (loginError == Boolean.TRUE) {
//            model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("authentication.login.error"));
//        }
//        if (logoutSuccess == Boolean.TRUE) {
//            model.addAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("authentication.logout.success"));
//        }
//        return "authentication/login";
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request){
//        HttpSession session = request.getSession();
//        session.invalidate();
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        securityContext.setAuthentication(null);
//        return "redirect:/login";
//    }
//
////    @GetMapping("/registrationConfirm")
////    public String confirmRegistration(@RequestParam("token") String token, ...) {
////        String result = userService.validateVerificationToken(token);
////        if(result.equals("valid")) {
////            User user = userService.getUser(token);
////            if (user.isUsing2FA()) {
////                model.addAttribute("qr", userService.generateQRUrl(user));
////                return "redirect:/.html?lang=" + locale.getLanguage();
////            }
////
////            model.addAttribute(
////                    "message", messages.getMessage("message.accountVerified", null, locale));
////            return "redirect:/login?lang=" + locale.getLanguage();
////        }
////    }
//
//}