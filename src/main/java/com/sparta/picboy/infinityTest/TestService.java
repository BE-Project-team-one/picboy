package com.sparta.picboy.infinityTest;

import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.post.PostProceedingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepsitory testRepsitory;

    public ResponseDto<?> test(int page, int size) {

        System.out.println(page + "");
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> findPost = testRepsitory.findAllByOrderByCreatedAtDesc(pageable);

        List<PostProceedingResponseDto> postProceedingResponseDtoList = new ArrayList<>();
        for (Post post : findPost) {

            Long id = post.getId();
            String imgUrl = post.getImgUrl();
            String topic = post.getTopic();
            String nickname = post.getMember().getNickname();
            int status = post.getStatus();

            PostProceedingResponseDto postProceedingResponseDto = new PostProceedingResponseDto(id, imgUrl, topic, nickname, status);
            postProceedingResponseDtoList.add(postProceedingResponseDto);

        }

        TestResponseDto testResponseDto = new TestResponseDto(findPost.isLast(), findPost.getTotalPages(), postProceedingResponseDtoList);
        return ResponseDto.success(testResponseDto);

    }

}
