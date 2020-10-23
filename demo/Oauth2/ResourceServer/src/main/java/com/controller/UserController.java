package com.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @PreAuthorize("hasAnyAuthority('user')")
    @GetMapping("/query2")
    public String query2() {
        return "query2";
    }

    @PreAuthorize("hasAnyAuthority('root')")
    @GetMapping("/query1")
    public String query1() {
        return "query1";
    }
}
