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

    @PutMapping("/admin/userLock/{memberId}")
    public void userLock(@PathVariable Long memberId) {
        adminService.userLock(memberId);
    }

    @PutMapping("/admin/userClear/{memberId}")
    public void userClear(@PathVariable Long memberId) {
        adminService.userClear(memberId);
    }

}
