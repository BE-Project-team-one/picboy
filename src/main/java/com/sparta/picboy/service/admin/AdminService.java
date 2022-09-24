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

    // 총 유저 수
    public int userCount() {
        return memberRepository.findAllMember().size();
    }

    // 총 게시물 수
    public int postCount() {
        return postRepository.findAllPost().size();
    }
}
