package com.habday.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/funding")
    public String home() {
        return "인가 성공^^";
    }
}
