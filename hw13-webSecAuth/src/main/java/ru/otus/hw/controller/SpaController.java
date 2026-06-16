package ru.otus.hw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/",
            "/books",
            "/books/**",
            "/authors",
            "/authors/**",
            "/genres",
            "/genres/**",
            "/books/create",
            "/books/{id}/edit",
            "/authors/create",
            "/authors/{id}/edit",
            "/genres/create",
            "/genres/{id}/edit",
            "/login"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}