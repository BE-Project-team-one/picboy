package com.sparta.picboy.service.myPage;


import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.mypage.MypageRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import com.sparta.picboy.dto.response.mypage.MypageUserInfoResponseDto;
import com.sparta.picboy.repository.post.HidePostRepository;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final PostRelayRepository postRelayRepository;

    private final HidePostRepository hidePostRepository;

    private final AwsS3Service awsS3Service;

    public Map<String, Object> getMypage(int page){

        PageRequest pageRequest = PageRequest.of(page, 24);
        Slice<Post> posts = postRepository.findAll(pageRequest);

        Map<String, Object> data = new HashMap<>();
        data.put("isList", posts.isLast());
        System.out.println(data);
        return data;
    }

//    public Map<String, Object> getMypage(Long memberId, long page){
//        Member member = memberRepository.findById(memberId).orElse(null);
//        PageRequest pageRequest = PageRequest.of((int) page, 24);
//        Slice<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("isList", posts.isLast());
//        return data;
//    }


    // 게시물 조회
    // tabNum : 전체0/작성1/"참여2"/숨김3
    // category : 최신1/좋아요2/댓글3
    public ResponseDto getMypagePost(String nickname, int tabNum, int categoryNum) {
//        String nickname = requestDto.getNickname();
        if(!memberRepository.existsByNickname(nickname))
            return ResponseDto.fail("NOT_FOUND", "회원정보를 가져올 수 없습니다.");
        List<Post> postList = new ArrayList<>();
        switch (categoryNum) {
            case 1 : //최신순
                    switch (tabNum) {
                        case 0 :  // 전체조회
                            List<Post>postListAll1 = postRepository.findAllByOrderByCreatedAtDesc();
                            for (Post post : postListAll1) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) {// 닉네임과 포스트으로 조회
                                    if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                        if (!postList.contains(post)) {// 중복 제외
                                            postList.add(post);
                                        }
                                    }
                                }
                            }
                            break;
                        case 1 : //작성글 조회
                            List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname);
                            for (Post post : postListAll2) {
                                if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)){ ; // 숨김 제외
                                    postList.add(post);}
                            }
                            break;

                        case 2 : //참여글 조회
                            List<Post>postListAll3 = postRepository.findAllByOrderByCreatedAtDesc();
                            for (Post post : postListAll3) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) // 닉네임과 포스트으로 조회
                                    if(!post.getMember().getNickname().equals(nickname)) {// 최초 작성자 제외
                                        if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) {// 숨김 제외
                                            if (!postList.contains(post)) { // 중복 제외
                                                postList.add(post);
                                            }
                                        }
                                    }
                            }
                            break;
                        case 3 : // 숨김글 조회
                            List<Post>postListAll4 = postRepository.findAllByOrderByCreatedAtDesc();
                            for (Post post : postListAll4) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) {
                                    if (hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김만 조회
                                        postList.add(post);
                                    }
                                }
                            }
                            break;
                    }
                    break;
            case 2 : // 좋아요 순
                    switch (tabNum) {
                        case 0 :  // 전체조회
                            List<Post>postListAll1 = postRepository.findAllByOrderByLikeCountDesc();
                            for (Post post : postListAll1) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) { // 닉네임과 포스트으로 조회
                                    if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) {// 숨김 제외
                                        if (!postList.contains(post)) {// 중복 제외
                                            postList.add(post);
                                        }
                                    }
                                }
                            }
                            break;
                        case 1 : //작성글 조회
                            List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname);
                            for (Post post : postListAll2) {
                                if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                    postList.add(post);
                                }
                            }
                            break;

                        case 2 : //참여글 조회
                            List<Post>postListAll3 = postRepository.findAllByOrderByLikeCountDesc();
                            for (Post post : postListAll3) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) {// 닉네임과 포스트으로 조회
                                    if (!post.getMember().getNickname().equals(nickname)) {// 최초 작성자 제외
                                        if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                            if (!postList.contains(post)) {// 중복 제외
                                                postList.add(post);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case 3 : // 숨김글 조회
                            List<Post>postListAll4 = postRepository.findAllByOrderByLikeCountDesc();
                            for (Post post : postListAll4) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) {
                                    if (hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) {  // 숨김만 조회
                                        postList.add(post);
                                    }
                                }
                            }
                            break;
                    }
                    break;
            case 3 :// 코멘트 순
                    switch (tabNum) {
                        case 0 :  // 전체조회
                            List<Post>postListAll1 = postRepository.findAllByOrderByCommentCountDesc();
                            for (Post post : postListAll1) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) {// 닉네임과 포스트으로 조회
                                    if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                        if (!postList.contains(post)) { // 중복 제외
                                            postList.add(post);
                                        }
                                    }
                                }
                            }
                            break;
                        case 1 : //작성글 조회
                            List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname);
                            for (Post post : postListAll2) {
                                if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                    postList.add(post);
                                }
                            }
                            break;

                        case 2 : //참여글 조회
                            List<Post>postListAll3 = postRepository.findAllByOrderByCommentCountDesc();
                            for (Post post : postListAll3) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) { // 닉네임과 포스트으로 조회
                                    if (!post.getMember().getNickname().equals(nickname)) {// 최초 작성자 제외
                                        if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) {// 숨김 제외
                                            if (!postList.contains(post)) { // 중복 제외
                                                postList.add(post);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case 3 : // 숨김글 조회
                            List<Post>postListAll4 = postRepository.findAllByOrderByCommentCountDesc();
                            for (Post post : postListAll4) {
                                if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) {
                                    if (hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김만 조회
                                        postList.add(post);
                                    }
                                }
                            }
                            break;
                    }
                    break;
        }

        List<MypageResponseDto> responseDtoList = new ArrayList<>();
        for (Post post : postList){
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            List<String> postNickList = new ArrayList<>();
            for (PostRelay postRelay : postRelayList)
                if (!postNickList.contains(postRelay.getMember().getNickname())) //참여자 중복 닉 제외
                     postNickList.add(postRelay.getMember().getNickname()); // 참여자 닉 리스트 생성

            if (post.getStatus() == 2) { // 완성된 움짤 gif
                responseDtoList.add(new MypageResponseDto(
                        post.getId(),
                        post.getGifUrl(), // gif
                        post.getLikeCount(),
                        post.getCommentCount(),
                        post.getViewCount(),
                        post.getReportCount(),
                        post.getTopic(),
                        post.getMember().getNickname(),
                        postNickList.size() - 1, // 글쓴이 외 참여자 수
                        post.getExpiredAt(),
                        post.getStatus()

                ));
            } else { // 그외 움짤 img
                responseDtoList.add(new MypageResponseDto(
                        post.getId(),
                        post.getImgUrl(), // img
                        post.getLikeCount(),
                        post.getCommentCount(),
                        post.getViewCount(),
                        post.getReportCount(),
                        post.getTopic(),
                        post.getMember().getNickname(),
                        postNickList.size() - 1, // 글쓴이 외 참여자 수
                        post.getExpiredAt(),
                        post.getStatus()

                ));
            }
        }
        return ResponseDto.success(responseDtoList);
    }

    //게시글 참여자 조회
    public ResponseDto getPartipants(Long postIid){
        Post post = postRepository.findById(postIid).orElse(null);
        if(post == null){
            return ResponseDto.fail("NOT_FOUND", "게시글이 존재하지 않습니다.");
        }
        List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
        List<String> postNickList = new ArrayList<>();
        for (PostRelay postRelay : postRelayList)
            if (!postNickList.contains(postRelay.getMember().getNickname())) //참여자 중복 닉 제외
                postNickList.add(postRelay.getMember().getNickname()); // 참여자 닉 리스트 생성
        return ResponseDto.success(postNickList);
    }

    public ResponseDto<?> getUserInfo(String nickname){
        //String nickname = requestDto.getNickname();
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if (!memberRepository.existsByNickname(nickname)){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않은 닉네임입니다.");
        }

        // 총개시글 추출 : “게시물 갯수” ← 쓴글 + 참여한글
        List<Post> postList = new ArrayList<>();
        List<Post>postListAll3 = postRepository.findAllByOrderByCommentCountDesc();
        for (Post post : postListAll3) {
            if (postRelayRepository.existsByMember_NicknameAndPost(nickname, post)) { // 닉네임과 포스트으로 조회
                //if (!post.getMember().getNickname().equals(nickname)) {// 최초 작성자 제외
                //if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) {// 숨김 제외
                if (!postList.contains(post)) { // 중복 제외
                    postList.add(post);
                }
            }
        }
        MypageUserInfoResponseDto userInfoResponseDto = new MypageUserInfoResponseDto(
                member.getUsername(),
                member.getNickname(),
                member.getProfileImg(),
                postList.size()
        );
        return ResponseDto.success(userInfoResponseDto);
    }

    @Transactional
    public ResponseDto<?> updateUserInfo(UserDetails userinfo, MypageRequestDto requestDto, MultipartFile file) {
        Member member = memberRepository.findByNickname(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail("NOT_FIND_MEMBER", "유저를 찾을 수 없습니다.");
        String nickname = requestDto.getNickname();
        String imageUrl = getFileUrl(file, 1);
        if (imageUrl == null) return ResponseDto.fail("FAIL_UPLOAD", "파일 업로드를 실패했습니다.");

        member.update(nickname, imageUrl);
        memberRepository.save(member);
        return ResponseDto.success("회원정보를 수정하였습니다.");

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
