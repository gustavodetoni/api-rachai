package com.racha.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "HealthCheck", description = "Endpoints de check!")
public class HealthCheckController {
    
    @GetMapping("/check")
    public String check() {
        return "Rodando Rachai!";
    }
}