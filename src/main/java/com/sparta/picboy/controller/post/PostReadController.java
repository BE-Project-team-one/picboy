package com.sparta.picboy.controller.post;

import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.post.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostReadController {

    private final PostReadService postReadService;

    // 메인페이지 베스트 움짤 Top 10 보이기
    @GetMapping("/main/best-top10")
    public ResponseDto<?> mainTop10() {
        return postReadService.mainTop10();

    }

    // 로그인한 유저 정보 가져오기 <- 병합 후 유저 컨트롤러로 이동시키기
    @GetMapping("/main/user-info")
    public ResponseDto<?> loginUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postReadService.loginUserInfo(userDetails);

    }

    // 진행중인 움짤 페이지 목록 조회
    @GetMapping("/post/gif/images/{tabNum}")
    public ResponseDto<?> readProceeding(@PathVariable Long tabNum) {
        return postReadService.readProceeding(tabNum);

    }

    // 진행중인 움짤 디테일 페이지
    @GetMapping("/post/gif/images/detail/{postid}")
    public ResponseDto<?> readProceedingDetail(@PathVariable Long postid) {
        return postReadService.readProceedingDetail(postid);

    }

    // 완료된 움짤 페이지 목록 조회
    @GetMapping("/post/gif/{categoryNum}")
    public ResponseDto<?> readCompletion(@PathVariable Long categoryNum) {
        return postReadService.readCompletion(categoryNum);

    }

    // 완료된 움짤 페이지 제시어 o 목록 조회
    @GetMapping("/post/gif/topic-ok/{categoryNum}")
    public ResponseDto<?> readCompletionTopicOk(@PathVariable Long categoryNum) {
        return postReadService.readCompletionTopicOk(categoryNum);

    }

    // 완료된 움짤 페ㅣ지 제시어 x 목록 조회
    @GetMapping("/post/gif/topic-no/{categoryNum}")
    public ResponseDto<?> readCompletionTopicNull(@PathVariable Long categoryNum) {
        return postReadService.readCompletionTopicNull(categoryNum);

    }

    // 완료된 움짤 디테일 페이지
    @GetMapping("/post/gif/detail/{postid}")
    public ResponseDto<?> readCompletionDetail(@PathVariable Long postid) {
        return postReadService.readCompletionDetail(postid);

    }

}
