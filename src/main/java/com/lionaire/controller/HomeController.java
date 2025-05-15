package com.lionaire.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public String home() {
        return "Welcome to lionaire trading";
    }

    @GetMapping("/api")
    public String test() {
        return "api";
    }
}
