package com.sparta.picboy.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {


    // 관리자 메인 홈페이지
    @GetMapping("/admin")
    public String home() {
        return "index";
    }

    @GetMapping("/admin/user")
    public String adminUser() {
        return "userInfo";
    }
}
