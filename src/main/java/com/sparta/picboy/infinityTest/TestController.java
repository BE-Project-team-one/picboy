package com.sparta.picboy.infinityTest;

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
    public ResponseDto<?> test(@RequestParam int size, @RequestParam int page) {
        return testService.test(page, size);
    }

    }
