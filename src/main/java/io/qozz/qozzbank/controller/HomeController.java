package io.qozz.qozzbank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Home Controller
 * 
 * Simple controller to test if the application is working.
 * 
 * Endpoints:
 * - GET / - Returns a welcome message
 */
@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * Home Endpoint
     * 
     * Returns a simple welcome message to verify the application is running.
     * 
     * @return Welcome message
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of(
                "message", "Welcome to QozzBank!",
                "status", "Application is running"
        ));
    }
}

