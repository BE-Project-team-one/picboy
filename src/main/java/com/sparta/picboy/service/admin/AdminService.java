package com.sparta.picboy.service.admin;

import com.sparta.picboy.dto.response.post.PostResponseDto;
import com.sparta.picboy.dto.response.user.UserResponseDto;
import com.sparta.picboy.repository.queryDsl.MemberRepositoryImpl;
import com.sparta.picboy.repository.queryDsl.PostRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepositoryImpl memberRepository;
    private final PostRepositoryImpl postRepository;

    // 전체 회원 찾기
    public List<UserResponseDto> findAllUser() {
        return memberRepository.findAllMember();
    }


    // 전체 게시물 찾기
    public List<PostResponseDto> findAllPost() {
        return postRepository.findAllPost();
    }

    // 유저 계정 Lock
    @Transactional
    public void userLock(Long memberId) {
        memberRepository.userLock(memberId);
    }

    // 유저 계정 잠금 해제
    @Transactional
    public void userClear(Long memberId) {
        memberRepository.userClear(memberId);
    }

    // 총 유저 수 count
    public int userCount() {
        return memberRepository.findAllMember().size();
    }

    // 총 게시물 수 count
    public int postCount() {
        return postRepository.findAllPost().size();
    }

    // 주제어 있는 게시물 count
    public int topicIsNotNullPost() {
        return postRepository.topicIsNotNullPost();
    }

    // 주제어 없는 게시물 count
    public int topicIsNullPost() {
        return postRepository.topicIsNullPost();
    }

    // 완성된 게시물 count
    public int completePost() {
        return postRepository.completePost();
    }
    // 진행중인 게시물 count
    public int proceedingPost() {
        return postRepository.proceedingPost();
    }

    // 숨겨진 게시글 count
    public int hidnPost() {
        return postRepository.hidnPost();
    }

    // 오늘 가입된 유저 count
    public int todayRegister() {
        return memberRepository.todayRegister();
    }

    // 오늘 생성된 게시물 count
    public int todayCreatePost() {
        return postRepository.todayCreatePost();
    }

    // 오늘 완성된 게시물 count
    public int todayCompletePost() {
        return postRepository.todayCompletePost();
    }

    // 오늘 삭제될 게시물 count
    public int todayDeletePost() {
        return postRepository.todayDeletePost();
    }


}
