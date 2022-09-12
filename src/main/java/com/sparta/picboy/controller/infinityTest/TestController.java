package com.sparta.picboy.controller.infinityTest;

import com.sparta.picboy.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/post/test")
    public ResponseDto<?> test(@RequestParam int page, @RequestParam int size, boolean isAsc, String sortBy) {
        return testService.test(page, size, isAsc, sortBy);
    }

    }
