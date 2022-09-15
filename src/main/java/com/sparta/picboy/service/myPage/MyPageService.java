package com.sparta.picboy.service.myPage;


import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.mypage.MypageRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.mypage.MypagePaticipantsResponseDto;
import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import com.sparta.picboy.dto.response.mypage.MypageResultResponseDto;
import com.sparta.picboy.dto.response.mypage.MypageUserInfoResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.repository.post.HidePostRepository;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    // 게시물 조회
    // tabNum : 전체0/작성1/"참여2"/숨김3
    // category : 최신1/좋아요2/댓글3
    public ResponseDto getMypageAndUserInfoPost(String nickname, int tabNum, int categoryNum, int page, int size) {
        //Pageable pageable = PageRequest.of(page, 20);
//        String nickname = requestDto.getNickname();
        Pageable pageable = PageRequest.of(page, size);
        if(!memberRepository.existsByNickname(nickname))
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        List<Post> postList = new ArrayList<>();
        switch (categoryNum) {
            case 1 : //최신순
                    switch (tabNum) {
                        case 0 :  // 전체조회
                            List<Post>postListAll1 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
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
                            List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
                            for (Post post : postListAll2) {
                                if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)){  // 숨김 제외
                                    postList.add(post);}
                            }
                            break;

                        case 2 : //참여글 조회
                            List<Post>postListAll3 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
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
                            List<Post>postListAll4 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
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
                            List<Post>postListAll1 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
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
                            List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
                            for (Post post : postListAll2) {
                                if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                    postList.add(post);
                                }
                            }
                            break;

                        case 2 : //참여글 조회
                            List<Post>postListAll3 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
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
                            List<Post>postListAll4 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
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
                            List<Post>postListAll1 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
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
                            List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
                            for (Post post : postListAll2) {
                                if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                    postList.add(post);
                                }
                            }
                            break;

                        case 2 : //참여글 조회
                            List<Post>postListAll3 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
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
                            List<Post>postListAll4 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
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

        Member member = memberRepository.findByNickname(nickname).orElse(null);
        MypageUserInfoResponseDto userInfoResponseDto =
                new MypageUserInfoResponseDto(
                member.getUsername(),
                member.getNickname(),
                member.getProfileImg(),
                postList.size()
        );
        MypageResultResponseDto mypageResultResponseDto = new MypageResultResponseDto(
                userInfoResponseDto,
                responseDtoList);
        return ResponseDto.success(mypageResultResponseDto);
    }


    public ResponseDto getMypagePost(String nickname, int tabNum, int categoryNum, int page, int size) {
        //Pageable pageable = PageRequest.of(page, 20);
//        String nickname = requestDto.getNickname();
        Pageable pageable = PageRequest.of(page, size);
        if(!memberRepository.existsByNickname(nickname))
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        List<Post> postList = new ArrayList<>();
        switch (categoryNum) {
            case 1 : //최신순
                switch (tabNum) {
                    case 0 :  // 전체조회
                        List<Post>postListAll1 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
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
                        List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
                        for (Post post : postListAll2) {
                            if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)){  // 숨김 제외
                                postList.add(post);}
                        }
                        break;

                    case 2 : //참여글 조회
                        List<Post>postListAll3 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
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
                        List<Post>postListAll4 = postRepository.findAllByMember_NicknameOrderByCreatedAtDesc(nickname, pageable);
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
                        List<Post>postListAll1 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
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
                        List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
                        for (Post post : postListAll2) {
                            if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                postList.add(post);
                            }
                        }
                        break;

                    case 2 : //참여글 조회
                        List<Post>postListAll3 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
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
                        List<Post>postListAll4 = postRepository.findAllByMember_NicknameOrderByLikeCountDesc(nickname, pageable);
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
                        List<Post>postListAll1 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
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
                        List<Post>postListAll2 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
                        for (Post post : postListAll2) {
                            if (!hidePostRepository.existsByMember_NicknameAndPost(nickname, post)) { // 숨김 제외
                                postList.add(post);
                            }
                        }
                        break;

                    case 2 : //참여글 조회
                        List<Post>postListAll3 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
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
                        List<Post>postListAll4 = postRepository.findAllByMember_NicknameOrderByCommentCountDesc(nickname, pageable);
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
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }
        List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
        List<String> postNickList = new ArrayList<>();
        List<MypagePaticipantsResponseDto> mypagePaticipantsResponseDtoList = new ArrayList<>();
        for (PostRelay postRelay : postRelayList)
            if (!postNickList.contains(postRelay.getMember().getNickname())){ //참여자 중복 닉 제외
                postNickList.add(postRelay.getMember().getNickname()); // 참여자 닉 리스트 생성
                mypagePaticipantsResponseDtoList.add(new MypagePaticipantsResponseDto(
                        postRelay.getMember().getNickname(),
                        postRelay.getMember().getProfileImg()
                ));
            }
        return ResponseDto.success(mypagePaticipantsResponseDtoList);
    }

    public ResponseDto<MypageUserInfoResponseDto> getUserInfo(String nickname){
        //String nickname = requestDto.getNickname();
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if (!memberRepository.existsByNickname(nickname)){
            new ResponseEntity("NOT_FOUND_MEMBER", HttpStatus.NOT_FOUND);
        }

        // 총개시글 추출 : “게시물 갯수” ← 쓴글 + 참여한글
        List<Post> postList = new ArrayList<>();
        List<Post>postListAll = postRepository.findAllByOrderByCreatedAtDesc();
        for (Post post : postListAll) {
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
    public ResponseDto<?> updateNickname(UserDetails userinfo, String nickname) {
        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        member.updateNickname(nickname);
        memberRepository.save(member);
        return ResponseDto.success("닉네임을 수정하였습니다.");
    }

    @Transactional
    public ResponseDto<?> updateimage(UserDetails userinfo, MultipartFile file) {
        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        if (member.getProfileImg().equals(null)) {
            String imageUrl = getFileUrl(file);
            if (imageUrl == null) return ResponseDto.fail(ErrorCode.FAIL_FILE_UPLOAD);
            member.updateImg(imageUrl);
            memberRepository.save(member);
        } else {
            String thisImgUrl = member.getProfileImg();
            String deleteUrl = thisImgUrl.substring(thisImgUrl.indexOf("picboy/mypageImg"));
            System.out.println(deleteUrl);
            //deleteUrl : picboy/mypageImg/4354c745-097f-4230-8f32-03e2347ae3d2-mg
            awsS3Service.deleteImage(deleteUrl);
            //amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
            String imageUrl = getFileUrl(file);
            if (imageUrl == null) return ResponseDto.fail(ErrorCode.FAIL_FILE_UPLOAD);
            member.updateImg(imageUrl);
            memberRepository.save(member);
        }

        //substring 예제
        //String finds = "abc-def";
        //String ans =  finds.substring(0,3); //abc
        //String ans2 =  finds.substring(4); //def

        return ResponseDto.success("이미지를 수정하였습니다.");
    }

    // 파일 업로드 url 값 가져오기
    public String getFileUrl(MultipartFile file) {
//        try {
//            return awsS3Service.uploadFiles(file, "picboy/mypageImg");
//        } catch (IOException e) {
//            return null;
//        }
        return null;
    }
}
