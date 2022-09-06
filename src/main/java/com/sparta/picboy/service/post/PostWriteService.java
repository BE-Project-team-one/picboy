package com.sparta.picboy.service.post;

import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.domain.RandomTopic;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.post.PostRequestDto;
import com.sparta.picboy.dto.response.RandomTopicResponseDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.post.RandomTopicRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PostWriteService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AwsS3Service awsS3Service;
    private final RandomTopicRepository randomTopicRepository;
    private final PostRelayRepository postRelayRepository;



    // 게시물 생성
    @Transactional
    public ResponseDto<?> createPost(UserDetails userinfo, PostRequestDto postRequestDto, MultipartFile file) {

        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");

        String imageUrl = getFileUrl(file, 1);
        if (imageUrl == null) return ResponseDto.fail("FAIL_UPLOAD", "파일 업로드를 실패했습니다.");

        Post post = new Post(postRequestDto.getTopic(), 1, postRequestDto.getFrameTotal(), imageUrl, 1, member);
        post = postRepository.save(post);
        post.updateExpiredAt(post.getCreatedAt());

        PostRelay postRelay = new PostRelay(post.getFrameNum(), post.getImgUrl(), member, post);
        postRelayRepository.save(postRelay);

        return ResponseDto.success("게시물 생성 완료");
    }

    //랜덤 제시어 조회
    public ResponseDto<?> randomTopic() {
        List<RandomTopic> randomTopicList = randomTopicRepository.findAll();
        Random random = new Random();
        int randomNum = random.nextInt(randomTopicList.size()) + 1;

        RandomTopic randomTopic = randomTopicRepository.findById((long) randomNum).orElse(null);
        RandomTopicResponseDto randomTopicResponseDto = new RandomTopicResponseDto(randomTopic.getTopic());

        return ResponseDto.success(randomTopicResponseDto);

    }


    // 이어 그리기 생성
    @Transactional
    public ResponseDto<?> relayPost(Long postId, MultipartFile file, UserDetails userinfo) {
        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return ResponseDto.fail("NOT_FIND_POST", "게시물을 찾을 수 없습니다.");

        String imageUrl = getFileUrl(file, 1);
        if (imageUrl == null) return ResponseDto.fail("FAIL_UPLOAD", "파일 업로드를 실패했습니다.");

        post.frameUpdate(post.getFrameNum() + 1);
        post.imgUpdate(imageUrl);

        if (post.getStatus() == 2) return ResponseDto.fail("", "이미 완료된 게시물입니다.");
        if (post.getFrameTotal() == post.getFrameTotal()) post.statusUpdate(2);

        PostRelay postRelay = new PostRelay(post.getFrameNum(), post.getImgUrl(), member, post);
        postRelayRepository.save(postRelay);

        return ResponseDto.success("이어그리기 성공");

    }

    //게시물에 완성된 gif 파일 저장
    @Transactional
    public ResponseDto<?> gifSave(Long postId, MultipartFile file) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return ResponseDto.fail("NOT_FIND_POST", "게시물을 찾을 수 없습니다.");

        String imageUrl = getFileUrl(file, 2);
        if (imageUrl == null) return ResponseDto.fail("FAIL_UPLOAD", "파일 업로드를 실패했습니다.");

        post.updateGif(imageUrl);

        return ResponseDto.success("움짤 저장 성공");
    }


    // 파일 업로드 url 값 가져오기
    public String getFileUrl(MultipartFile file, int num) {
        try {
            if (num == 2) return awsS3Service.uploadFiles(file, "picboy/gif");
            return awsS3Service.uploadFiles(file, "picboy/images");

        } catch (IOException e) {
            return null;
        }
    }
}