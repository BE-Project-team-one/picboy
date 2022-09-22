package com.sparta.picboy.service.post;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.WebSocket.AlarmService;
import com.sparta.picboy.WebSocket.MessageDto;
import com.sparta.picboy.converter.GifSequenceWriter;
import com.sparta.picboy.domain.RandomTopic;
import com.sparta.picboy.domain.post.*;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.post.PostDelayRequestDto;
import com.sparta.picboy.dto.request.post.PostRequestDto;
import com.sparta.picboy.dto.response.RandomTopicResponseDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostReportRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.post.RandomTopicRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostWriteService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final AwsS3Service awsS3Service;
    private final RandomTopicRepository randomTopicRepository;
    private final PostRelayRepository postRelayRepository;
    private final AlarmService alarmService;
    private final PostReportRepository postReportRepository;
    private final EntityManager entityManager;



    // 게시물 생성
    @Transactional
    public ResponseDto<?> createPost(UserDetails userinfo, PostRequestDto postRequestDto) {

        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);

        Post post = new Post(postRequestDto.getTopic(), 1, postRequestDto.getFrameTotal(), "", 1, member);
        post = postRepository.save(post);

        String imageUrl = getFileUrl(postRequestDto.getFile(), post.getId());
        if (imageUrl == null) return ResponseDto.fail(ErrorCode.FAIL_FILE_UPLOAD);

        // 삭제 날짜 설정
        post.updateExpiredAt(post.getCreatedAt());
        // 이미지 저장
        post.imgUpdate(imageUrl);

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
        if(randomTopic == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_TOPIC);
        RandomTopicResponseDto randomTopicResponseDto = new RandomTopicResponseDto(randomTopic.getTopic());

        return ResponseDto.success(randomTopicResponseDto);

    }


    // 이어 그리기 생성
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public ResponseDto<?> relayPost(Long postId, PostDelayRequestDto postDelayRequestDto, UserDetails userinfo) {
        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);

//        Post post = postRepository.findById(postId).orElse(null);
        Post post = this.entityManager.find(Post.class, postId, LockModeType.PESSIMISTIC_WRITE, Map.of("javax.persistence.lock.timeout", 1L));
        if (post == null) return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        if (post.getStatus() == 2) return ResponseDto.fail(ErrorCode.ALREADY_COMPLETED_POST);

        String imageUrl = getFileUrl(postDelayRequestDto.getFile(), postId);
        if (imageUrl == null) return ResponseDto.fail(ErrorCode.FAIL_FILE_UPLOAD);

        post.frameUpdate(post.getFrameNum() + 1);
        post.imgUpdate(imageUrl);

        PostRelay postRelay = new PostRelay(post.getFrameNum(), post.getImgUrl(), member, post);
        postRelayRepository.save(postRelay);

        if (post.getFrameNum() == post.getFrameTotal()) {
            post.statusUpdate(2);

            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);

            try {
                createGif(post,postRelayList);
            } catch (IOException e) {
                // gif 생성 실패 오류 메시지 만들기
                throw new RuntimeException(e.getMessage());
            }
        }

        // 게시물이 완성됬을 때 알람메시지 작동
        if(post.getStatus() == 2) {
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            Set<Member> memberSet = new HashSet<>();

            for(PostRelay relay : postRelayList) {
                memberSet.add(relay.getMember());
            }

            MessageDto messageDto = new MessageDto(memberSet, "게시물이 완성되었습니다", post.getId());
            alarmService.alarmByMessage(messageDto);
        }

        return ResponseDto.success("이어그리기 성공");

    }

    //게시물에 완성된 gif 파일 저장
    @Transactional
    public void gifSave(Post post, File file) {
        // String imageUrl = getFileUrl(file, 2, null);
        String gifUrl = getFileGifUrl(file,post.getId());
        if (gifUrl == null) return;

        post.updateGif(gifUrl);
    }


    // 파일 업로드 url 값 가져오기
    public String getFileUrl(String file, Long postId) {
        try {
            return awsS3Service.uploadFiles(file, "picboy/images/post" + postId);
        } catch (IOException e) {
            return null;
        }
    }
    // gif url 값 가져오기
    public String getFileGifUrl(File file, Long postId) {
        return awsS3Service.upload(file,"picboy/gif/post" + postId);
    }

    @Transactional
    // 게시물 삭제
    public ResponseDto<?> postDelete(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);

        postRepository.delete(post);
        awsS3Service.removeFolder("picboy/images/post" + post.getId());

        List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
        Set<Member> memberSet = new HashSet<>();

        for(PostRelay relay : postRelayList) {
            memberSet.add(relay.getMember());
        }
        MessageDto messageDto = new MessageDto(memberSet, "게시물이 삭제되었습니다.", post.getId());
        alarmService.alarmByMessage(messageDto);

        return ResponseDto.success("게시물이 삭제되었습니다.");
    }


    //gif 파일 생성
    @Transactional
    public void createGif(Post post, List<PostRelay> postRelayList) throws IOException {
        URL[] images = new URL[postRelayList.size()];
        for(int i = 0; i < postRelayList.size(); i++) {
            images[i] = new URL(postRelayList.get(i).getImgUrl());
        }
        BufferedImage first = ImageIO.read(new URL(images[0].toString()));

        File convertFile = new File(System.getProperty("user.dir") + "/" + "temporaryFile.gif");
        ImageOutputStream output = new FileImageOutputStream(convertFile);
        GifSequenceWriter writer = new GifSequenceWriter(output, first.getType(), 350, true);
        writer.writeToSequence(first);

        for (URL image : images) {
            BufferedImage next = ImageIO.read(image);
            writer.writeToSequence(next);
        }

        writer.close();
        output.close();

        gifSave(post, convertFile);
    }

    // 신고하기
    @Transactional
    public ResponseDto<?> postReport(Long postId, UserDetails userDetails) {

        // 존재하는 유저인지 확인
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);

        if (member == null) {
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);

        }

        // 존재하는 게시물인지 확인
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) { // 게시물이 존재하지 않은경우
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }

        // 신고하기
        if(!postReportRepository.existsByPostAndMember(post, member)) {

            Report report = new Report(member, post);
            postReportRepository.save(report);

            List<Report> postReportList = postReportRepository.findAllByPost(post);
            post.updateReportCnt(postReportList.size());

            return ResponseDto.success("신고 완료");

        } else { // 신고하기 취소

            postReportRepository.deleteByPostAndMember(post, member);

            List<Report> postReportList = postReportRepository.findAllByPost(post);
            post.updateReportCnt(postReportList.size());

            return ResponseDto.success("신고 취소");

        }

    }
}