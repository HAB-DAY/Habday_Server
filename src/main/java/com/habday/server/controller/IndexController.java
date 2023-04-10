package com.habday.server.controller;

import com.habday.server.config.auth.LoginUser;
import com.habday.server.config.auth.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class IndexController {

    @GetMapping("/auth")
    public String index(Model model, @LoginUser SessionMember member) {
        //model.addAttribute()
        if(member != null) {
            model.addAttribute("memberName", member.getName());
        }
        return "index";
    }
}
