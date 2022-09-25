package com.sparta.picboy.scheduler;

import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.WebSocket.AlarmService;
import com.sparta.picboy.WebSocket.MessageDto;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private final PostRepository postRepository;

    private final AwsS3Service awsS3Service;

    private final PostRelayRepository postRelayRepository;
    private final AlarmService alarmService;

    // 초, 분, 시, 일, 월, 주 순서
    // 영속성 컨테이너 관리 때문에 @Transactional을 꼭 사용해야함
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePost() throws InterruptedException {
        List<Post> postList = postRepository.findAll();

        LocalDateTime currentDateTime = LocalDateTime.now();

        for(Post post : postList) {
            // 오늘 날짜가 삭제일보다 크고 게시물 상태가 1(미완성)일때
            //
            if(currentDateTime.isAfter(post.getExpiredAt()) && post.getStatus() == 1) {

                List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
                Set<Member> memberSet = new HashSet<>();

                for(PostRelay relay : postRelayList) {
                    memberSet.add(relay.getMember());
                }
                postRepository.delete(post);
                awsS3Service.removeFolder("picboy/images/post" + post.getId());

                MessageDto messageDto = new MessageDto(memberSet, "게시물이 삭제되었습니다.", post.getId());
                alarmService.alarmByMessage(messageDto);
                logger.info("게시물 <"+post.getId()+">번이 삭제되었습니다.");
            }
        }
    }
}
