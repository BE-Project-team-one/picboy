package com.sparta.picboy.service.myPage;

import com.sparta.picboy.S3Upload.AwsS3Service;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.mypage.MypageImageRequestDto;
import com.sparta.picboy.dto.request.mypage.MypageRequestDto;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.mypage.MypagePaticipantsResponseDto;
import com.sparta.picboy.dto.response.mypage.MypageUserInfoResponseDto;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.queryDsl.PostRepositoryImpl;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final PostRelayRepository postRelayRepository;

    private final AwsS3Service awsS3Service;
    private final PostRepositoryImpl postQueryDsl;



    // 마이페이지 게시물 조회
    // tabNum : 전체0/작성1/"참여2"/숨김3
    // category : 최신1/좋아요2/댓글3
    public ResponseDto<?> getMypagePost(String username, int tabNum, int categoryNum, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseDto.success(postQueryDsl.categorySort(username,tabNum, categoryNum,pageable));
    }


    //게시글 참여자 조회
    public ResponseDto<?> getPartipants(Long postIid){
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

    public ResponseDto<MypageUserInfoResponseDto> getUserInfo(String username){
        //String nickname = requestDto.getNickname();
        Member member = memberRepository.findByUsername(username).orElse(null);
        if(member == null) {
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        }

        // 총개시글 추출 : “게시물 갯수” ← 쓴글 + 참여한글
        List<Post> postList = new ArrayList<>();
        List<Post>postListAll = postRepository.findAllByOrderByCreatedAtDesc();
        for (Post post : postListAll) {
            if (postRelayRepository.existsByMember_UsernameAndPost(username, post)) { // 닉네임과 포스트으로 조회
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
    public ResponseDto<?> updateNickname(UserDetails userinfo, MypageRequestDto requestDto) {
        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        String nickname = requestDto.getNickname();
        member.updateNickname(nickname);
        memberRepository.save(member);
        return ResponseDto.success("닉네임을 수정하였습니다.");
    }

    @Transactional
    public ResponseDto<?> updateimage(UserDetails userinfo, MypageImageRequestDto requestDto) {
        Member member = memberRepository.findByUsername(userinfo.getUsername()).orElse(null);
        if (member == null) return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        System.out.println(member.getProfileImg());
        if (member.getProfileImg() == null) {
            String imageUrl = getFileUrl(requestDto.getImg());
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
            String imageUrl = getFileUrl(requestDto.getImg());
            if (imageUrl == null) return ResponseDto.fail(ErrorCode.FAIL_FILE_UPLOAD);
            member.updateImg(imageUrl);
            memberRepository.save(member);
        }


        return ResponseDto.success("이미지를 수정하였습니다.");
    }

    // 파일 업로드 url 값 가져오기
    public String getFileUrl(String file) {
        try {
            return awsS3Service.uploadFiles(file, "picboy/mypageImg");
        } catch (IOException e) {
            return null;
        }
    }
}
