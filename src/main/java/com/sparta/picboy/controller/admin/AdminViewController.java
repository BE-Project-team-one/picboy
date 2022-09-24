package com.sparta.picboy.controller.admin;

import com.sparta.picboy.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
@RequiredArgsConstructor
@Controller
public class AdminViewController {

    private final AdminService adminService;

    // 관리자 메인 홈페이지
    @GetMapping("/admin")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        // 총 유저수
        mv.addObject("userCount", adminService.userCount());
        // 총 게시물 수
        mv.addObject("postCount", adminService.postCount());

        return mv;
    }

    // 관리자 유저 페이지
    @GetMapping("/admin/user")
    public ModelAndView adminUser() {
        ModelAndView mv = new ModelAndView();

        mv.addObject("userList", adminService.findAllUser() );
        mv.setViewName("userInfo");
        return mv;
    }

    // 관리자 정보 페이지
    @GetMapping("/admin/post")
    public ModelAndView adminPost() {
        ModelAndView mv = new ModelAndView();

        mv.addObject("postList", adminService.findAllPost());
        mv.setViewName("postInfo");

        return mv;
    }


}
