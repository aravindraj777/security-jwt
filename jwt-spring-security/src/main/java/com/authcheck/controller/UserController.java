package com.authcheck.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


    @GetMapping
    public ResponseEntity<String> sayHello(){
        System.out.println("Cominggggggg");

        return ResponseEntity.ok("Hii user");
    }
}
