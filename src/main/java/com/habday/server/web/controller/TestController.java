package com.habday.server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/funding")
    public String home() {
        System.out.println("/funding 컨트롤러 들어옴");
        return "인가 성공^^";
    }
}
