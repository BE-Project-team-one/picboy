package com.sparta.picboy.controller.admin;

import com.sparta.picboy.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Controller
public class AdminViewController {

    private final AdminService adminService;

    @Value("${admin_id}")
    private String  adminId;

    @Value("${admin_password}")
    private String adminPassword;

    @Value("${admin_token}")
    private String adminToken;


    // 관리자 메인 홈페이지 (첫 로그인시 세팅)
    @PostMapping("/admin")
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView();
        String id = request.getParameter("username");
        String password = request.getParameter("password");

        if(id == null || password == null) {
            mv.setViewName("login");
            return mv;
        }
        mv.setViewName("index");
        // 총 유저수
        mv.addObject("userCount", adminService.userCount());
        // 총 게시물 수
        mv.addObject("postCount", adminService.postCount());
        // 주제어 있는 게시물
        mv.addObject("topicIsNotNullPost", adminService.topicIsNotNullPost());
        // 주제어 없는 게시물
        mv.addObject("topicIsNullPost", adminService.topicIsNullPost());
        // 완성된 게시물
        mv.addObject("completePost", adminService.completePost());
        // 진행중인 게시물
        mv.addObject("proceedingPost", adminService.proceedingPost());
        // 숨겨진 게시글
        mv.addObject("hidnPost", adminService.hidnPost());
        // 오늘 가입한 유저
        mv.addObject("todayRegister", adminService.todayRegister());
        // 오늘 생성된 게시물
        mv.addObject("todayCreatePost", adminService.todayCreatePost());
        // 오늘 완료된 게시물
        mv.addObject("todayCompletePost", adminService.todayCompletePost());
        // 오늘 삭제될 게시물
        mv.addObject("todayDeletePost", adminService.todayDeletePost());
        // cookie 토큰 값 저장
        mv.addObject("token", adminToken);

        System.out.println("post 홈");

        if(id.equals(adminId) && password.equals(adminPassword)) {
            Cookie myCookie = new Cookie("cookieName", adminToken);
            myCookie.setPath("/"); // 모든 경로에서 접근 가능 하도록 설정
            response.addCookie(myCookie);
        }

        return mv;
    }

    // 관리자 메인 홈페이지 get
    @GetMapping("/admin")
    public ModelAndView homeGet() {
        ModelAndView mv = new ModelAndView();
        System.out.println("get 홈");

        mv.setViewName("index");
        // 총 유저수
        mv.addObject("userCount", adminService.userCount());
        // 총 게시물 수
        mv.addObject("postCount", adminService.postCount());
        // 주제어 있는 게시물
        mv.addObject("topicIsNotNullPost", adminService.topicIsNotNullPost());
        // 주제어 없는 게시물
        mv.addObject("topicIsNullPost", adminService.topicIsNullPost());
        // 완성된 게시물
        mv.addObject("completePost", adminService.completePost());
        // 진행중인 게시물
        mv.addObject("proceedingPost", adminService.proceedingPost());
        // 숨겨진 게시글
        mv.addObject("hidnPost", adminService.hidnPost());
        // 오늘 가입한 유저
        mv.addObject("todayRegister", adminService.todayRegister());
        // 오늘 생성된 게시물
        mv.addObject("todayCreatePost", adminService.todayCreatePost());
        // 오늘 완료된 게시물
        mv.addObject("todayCompletePost", adminService.todayCompletePost());
        // 오늘 삭제될 게시물
        mv.addObject("todayDeletePost", adminService.todayDeletePost());
        // cookie 토큰 값 저장
        mv.addObject("token", adminToken);

        return mv;
    }


    // 관리자 유저 페이지
    @GetMapping("/admin/user")
    public ModelAndView adminUser() {
        ModelAndView mv = new ModelAndView();

        mv.addObject("userList", adminService.findAllUser() );
        mv.addObject("token", adminToken);
        mv.setViewName("userInfo");
        return mv;
    }

    // 관리자 정보 페이지
    @GetMapping("/admin/post")
    public ModelAndView adminPost() {
        ModelAndView mv = new ModelAndView();

        mv.addObject("postList", adminService.findAllPost());
        mv.addObject("token", adminToken);
        mv.setViewName("postInfo");

        return mv;
    }

    @GetMapping("/admin/login")
    public ModelAndView adminLogin() {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("login");
        return mv;
    }





}
