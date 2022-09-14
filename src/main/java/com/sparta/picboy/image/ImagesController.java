package com.sparta.picboy.image;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ImagesController {

    private final CreateGifExample createGifExample;

    @GetMapping("/post/test2")
    public String test2() throws IOException {
        return createGifExample.test2();

    }

}
