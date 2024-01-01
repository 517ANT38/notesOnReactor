package com.app.testingService.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/info")
public class InfoController {
    @GetMapping("/version")
    public Mono<String> version() {
        return Mono.just("1.0.0");
    }
}
