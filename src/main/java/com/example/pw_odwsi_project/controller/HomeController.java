package com.example.pw_odwsi_project.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/notes";
    }

}
