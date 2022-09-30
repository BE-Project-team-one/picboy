package com.sparta.picboy.controller.admin;

import com.sparta.picboy.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    // 유저 계정 잠금
    @PutMapping("/admin/userLock/{memberId}")
    public void userLock(@PathVariable Long memberId) {
        adminService.userLock(memberId);
    }
    // 유저 잠금 해제
    @PutMapping("/admin/userClear/{memberId}")
    public void userClear(@PathVariable Long memberId) {
        adminService.userClear(memberId);
    }

    // 신고 카운트 reset
    @PutMapping("/admin/reset/{postId}")
    public void reportCountReset(@PathVariable Long postId) {
        adminService.reportCountReset(postId);
    }

}
