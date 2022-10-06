package com.sparta.picboy.scheduler;

import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.service.post.PostWriteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private final PostRepository postRepository;
    private final PostWriteService postWriteService;

    // 초, 분, 시, 일, 월, 주 순서
    // 영속성 컨테이너 관리 때문에 @Transactional을 꼭 사용해야함
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePost() throws InterruptedException {
        List<Post> postList = postRepository.findAll();

        LocalDateTime currentDateTime = LocalDateTime.now();

        for(Post post : postList) {
            // 오늘 날짜가 삭제일보다 크고 게시물 상태가 1(미완성)일때
            if(currentDateTime.isAfter(post.getExpiredAt()) && post.getStatus() == 1) {
                postWriteService.postDelete(post.getId());
                logger.info("게시물 <"+post.getId()+">번이 삭제되었습니다.");
            }
        }
    }
}
