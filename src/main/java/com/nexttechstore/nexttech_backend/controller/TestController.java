package com.nexttechstore.nexttech_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/prueba")
    public String holaMundo() {
        return "¡Tu backend en Spring Boot está funcionando!";
    }
}