package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BasisController {
    @GetMapping("")
    Map<String, String> test() {
        return Map.of("Hello", "World");
    }
    @PostMapping
    void store(@RequestBody StoreRequest data) {
        System.out.println(data);
    }
}
