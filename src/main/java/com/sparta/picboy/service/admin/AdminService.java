package com.sparta.picboy.service.admin;

import com.sparta.picboy.dto.response.post.PostResponseDto;
import com.sparta.picboy.dto.response.user.UserResponseDto;
import com.sparta.picboy.repository.queryDsl.MemberRepositoryImpl;
import com.sparta.picboy.repository.queryDsl.PostRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
