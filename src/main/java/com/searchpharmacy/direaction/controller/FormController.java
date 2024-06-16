package com.searchpharmacy.direaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class FormController {

    @GetMapping("/")
    public String main() {
        return "main";
    }
}
