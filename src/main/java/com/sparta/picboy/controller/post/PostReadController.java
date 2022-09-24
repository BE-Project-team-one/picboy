package com.sparta.picboy.controller.post;

import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.service.post.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PostReadController {

    private final PostReadService postReadService;
    private final AwsS3Service awsS3Service;

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
    public ResponseDto<?> readProceeding(@PathVariable Long tabNum, @RequestParam int size, @RequestParam int page) {
        return postReadService.readProceeding(tabNum, size, page);

    }

    // 진행중인 움짤 디테일 페이지
    @GetMapping("/post/gif/images/detail/{postid}")
    public ResponseDto<?> readProceedingDetail(@PathVariable Long postid) {
        return postReadService.readProceedingDetail(postid);

    }


    // 완료된 움짤 페이지 조회
    @GetMapping("/post/gif/{tabNum}/{categoryNum}")
    public ResponseDto<?> testSibal(@PathVariable int tabNum, @PathVariable int categoryNum, @RequestParam int page, @RequestParam int size) {
        return postReadService.postRead(tabNum, categoryNum, page, size);

    }

    // 완료된 움짤 페이지 목록 조회
    @GetMapping("/post/gif/{categoryNum}")
    public ResponseDto<?> readCompletion(@PathVariable Long categoryNum, @RequestParam int size, @RequestParam int page) {
        return postReadService.readCompletion(categoryNum, size, page);

    }

    // 완료된 움짤 페이지 제시어 o 목록 조회
    @GetMapping("/post/gif/topic-ok/{categoryNum}")
    public ResponseDto<?> readCompletionTopicOk(@PathVariable Long categoryNum, @RequestParam int size, @RequestParam int page) {
        return postReadService.readCompletionTopicOk(categoryNum, size, page);

    }

    // 완료된 움짤 페이지 제시어 x 목록 조회
    @GetMapping("/post/gif/topic-no/{categoryNum}")
    public ResponseDto<?> readCompletionTopicNull(@PathVariable Long categoryNum, @RequestParam int size, @RequestParam int page) {
        return postReadService.readCompletionTopicNull(categoryNum, size, page);

    }

    // 완료된 움짤 디테일 페이지
    @GetMapping("/post/gif/detail/{postid}")
    public ResponseDto<?> readCompletionDetail(@PathVariable Long postid ,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean login = true;
        if (userDetails == null) {
            login = false;
            return postReadService.readCompletionDetail(postid, login);
        }

        return postReadService.readCompletionDetail(postid, login);

    }

    // 게시물 다운로드
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam Long postId, @RequestParam String fileName) throws IOException {
        return awsS3Service.getObject(postId, fileName);
    }

}
