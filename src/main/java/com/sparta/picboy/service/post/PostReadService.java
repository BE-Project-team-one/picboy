package com.sparta.picboy.service.post;

import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.domain.comment.Comment;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.domain.post.PostRelay;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.response.ResponseDto;
import com.sparta.picboy.dto.response.post.*;
import com.sparta.picboy.exception.ErrorCode;
import com.sparta.picboy.repository.comment.CommentRepository;
import com.sparta.picboy.repository.post.PostLikeRepository;
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostReadService {

    private final PostRepository postRepository;
    private final PostRelayRepository postRelayRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;


    // 메인페이지 베스트 움짤 Top 10
    public ResponseDto<?> mainTop10() {
        List<Post> postListTop3 = postRepository.findTop3ByStatusOrderByLikeCountDesc(2);
        List<Post> postListTop10 = postRepository.findTop10ByStatusOrderByLikeCountDesc(2);
        List<PostMainTop3ResponseDto> postMainTop3ResponseDtoList = new ArrayList<>();
        List<PostMainTop410ResponseDto> postMainTop410ResponseDtoList = new ArrayList<>();

        // 탑 10 넣기
        for (Post post : postListTop10) {

            Long id = post.getId();
            String gifUrl = post.getGifUrl();
            int likeCount = post.getLikeCount();
            String topic = post.getTopic();
            String nickname = post.getMember().getNickname();

            // 참가자 수를 구할건데 게시물에 연관된 릴레이 테이블의 게시물을 모두 불러와서 프레임별로 멤버를 뽑아낼것임
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            List<Member> memberList = new ArrayList<>();
            for (PostRelay postRelay : postRelayList) {

                // memberList 에 프레임별로 멤버를 add 해줄건데 중복 없에기 위한 작업을 진행. 지우고 넣는 방식 채택
                memberList.remove(postRelay.getMember()); //x 여태있던 멤버리스트를 다 지우고
                memberList.add(postRelay.getMember()); //x 추가된 멤버를 더해서 멤버리스트를 추가한다

            }

            // 리스폰스는 '작성자 외 n 명' 이라서 작성자는 따로 빼고 연산
            memberList.remove(post.getMember());  //x post 에서 가져온 멤버는 작성자 이므로 제거하면 -1이 됨
            int memberCount = memberList.size();

            PostMainTop410ResponseDto postMainTop410ResponseDto = new PostMainTop410ResponseDto(id, gifUrl, likeCount, topic, nickname, memberCount);
            postMainTop410ResponseDtoList.add(postMainTop410ResponseDto);

        }

        // 탑 3 넣기
        for (Post post : postListTop3) {

            Long id = post.getId();
            String gifUrl = post.getGifUrl();
            int likeCount = post.getLikeCount();
            String topic = post.getTopic();
            String nickname = post.getMember().getNickname();

            // 참가자 수를 구할건데 게시물에 연관된 릴레이 테이블의 게시물을 모두 불러와서 프레임별로 멤버를 뽑아낼것임
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            List<Member> memberList = new ArrayList<>();
            for (PostRelay postRelay : postRelayList) {

                // memberList 에 프레임별로 멤버를 add 해줄건데 중복 없에기 위한 작업을 진행. 지우고 넣는 방식 채택
                memberList.remove(postRelay.getMember()); //x 여태있던 멤버리스트를 다 지우고
                memberList.add(postRelay.getMember()); //x 추가된 멤버를 더해서 멤버리스트를 추가한다

            }

            // 리스폰스는 '작성자 외 n 명' 이라서 작성자는 따로 빼고 연산
            memberList.remove(post.getMember());  //x post 에서 가져온 멤버는 작성자 이므로 제거하면 -1이 됨
            int memberCount = memberList.size();

            PostMainTop3ResponseDto postMainTop3ResponseDto = new PostMainTop3ResponseDto(id, gifUrl, likeCount, topic, nickname, memberCount);
            postMainTop3ResponseDtoList.add(postMainTop3ResponseDto);

        }

        postMainTop410ResponseDtoList.remove(0);
        postMainTop410ResponseDtoList.remove(1);
        postMainTop410ResponseDtoList.remove(2);

        System.out.println(postMainTop410ResponseDtoList.size());

        PostMainTopResponseDto postMainTopResponseDto = new PostMainTopResponseDto(postMainTop3ResponseDtoList, postMainTop410ResponseDtoList);

        return ResponseDto.success(postMainTopResponseDto);

    }

    // 로그인 유저 정보 가져오기 <- 병합 후 유저 서비스에 옮겨놓기
    public ResponseDto<?> loginUserInfo(UserDetailsImpl userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        // 그럴 일이 없겠지만 혹여나 로그인한 유저의 정보를 못가져 왔을때를 위한 오류
        if (member == null) {
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        }

        String username = member.getUsername();
        String nickname = member.getNickname();
        String profileImg = member.getProfileImg();
        String authority = member.getAuthority();

        MemberLoginInfoResponseDto memberLoginInfoResponseDto = new MemberLoginInfoResponseDto(username, nickname, profileImg, authority);
        return ResponseDto.success(memberLoginInfoResponseDto);


    }

    // 진행중인 움짤 페이지 목록 조회
    public ResponseDto<?> readProceeding(Long tabNum, int size, int page) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postList = null;

        // 게시글 전체
        if (tabNum == 0) postList = postRepository.findAllByStatusOrderByModifiedAtDesc(1,pageable);

        // 주제어 o
        if (tabNum == 1) postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByModifiedAtDesc(1, pageable);

        // 주제어 x
        if (tabNum == 2) postList = postRepository.findAllByTopicIsNullAndStatusOrderByModifiedAtDesc(1, pageable);

        //x pageable을 controller에서 받아주면 안되는건가?

        return ResponseDto.success(sortProceedingCategory(postList));


    }

    // 진행중인 움짤 카테고리 정렬 중복 매서드 처리
    public List<PostProceedingResponseDto> sortProceedingCategory(Page<Post> postList) {

        List<PostProceedingResponseDto> postProceedingResponseDtoList = new ArrayList<>();
        for (Post post : postList) {

            Long id = post.getId();
            String imgUrl = post.getImgUrl();
            String topic = post.getTopic();
            String nickname = post.getMember().getNickname();
            int status = post.getStatus();
            String profileImg = post.getMember().getProfileImg();

            List<Member> memberList = new ArrayList<>();

            // 참가자 수를 구할건데 게시물에 연관된 릴레이 테이블의 게시물을 모두 불러와서 프레임별로 멤버를 뽑아낼것임
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            for (PostRelay postRelay : postRelayList) {

                Member member = postRelay.getMember();
                memberList.remove(member);
                memberList.add(member);
            }

            // 참가자 중에서 게시물 작성자 제외하기
            Member members = post.getMember();
            memberList.remove(members);

            int participantCount = memberList.size();

            List<ParticipantResponseDto> participantResponseDtoList = new ArrayList<>();

            // 생성된 멤버 명단에서 하나씩 돌면서 멤버정보 세분화하여 뽑아내기
            for (Member member : memberList) {

                ParticipantResponseDto participantResponseDto = new ParticipantResponseDto(member.getNickname(), member.getProfileImg());
                participantResponseDtoList.add(participantResponseDto);

            }

            PostProceedingResponseDto postProceedingResponseDto = new PostProceedingResponseDto(id, imgUrl, topic, nickname, status, profileImg, participantResponseDtoList, participantCount);
            postProceedingResponseDtoList.add(postProceedingResponseDto);

        }

        return postProceedingResponseDtoList;

    }

    // 진행중인 움짤 디테일 페이지 조회
    public ResponseDto<?> readProceedingDetail(Long postid) {

        Post post = postRepository.findById(postid).orElse(null);
        if(post == null) { // 찾는 게시물이 존재하지 않을때
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }

        // 프레임 리스트 작성
        List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
        List<FrameImgListResponseDto> frameImgListResponseDtoList = new ArrayList<>();
        for (PostRelay postRelay : postRelayList) {

            FrameImgListResponseDto frameImgListResponseDto = new FrameImgListResponseDto(postRelay);
            frameImgListResponseDtoList.add(frameImgListResponseDto);
        }

        PostProceedingDetailResponseDto postProceedingDetailResponseDto = new PostProceedingDetailResponseDto(post, frameImgListResponseDtoList);
        return ResponseDto.success(postProceedingDetailResponseDto);
    }


    // 완료된 움짤 페이지 목록 전체 조회
    public ResponseDto<?> readCompletion(Long categoryNum, int size, int page) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postList = null;

        // 최신 순 정렬
        if (categoryNum == 1) postList = postRepository.findAllByStatusOrderByCompletAtDesc(2, pageable);

        // 좋아요 순 정렬
        if (categoryNum == 2) postList = postRepository.findAllByStatusOrderByLikeCountDesc(2, pageable);

        // 댓글 수 순 정렬
        if (categoryNum == 3) postList = postRepository.findAllByStatusOrderByCommentCountDesc(2, pageable);

        // 조회 수 순 정렬
        if (categoryNum == 4) postList = postRepository.findAllByStatusOrderByViewCountDesc(2, pageable);

        return ResponseDto.success(sortCompletionCategory(postList));

    }

    // 완료된 움짤 페이지 목록 제시어 o 조회
    public ResponseDto<?> readCompletionTopicOk(Long categoryNum, int size, int page) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postList = null;

        // 최신 순 정렬
        if (categoryNum == 1) postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByCompletAtDesc(2, pageable);

        // 좋아요 순 정렬
        if (categoryNum == 2) postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByLikeCountDesc(2, pageable);

        // 댓글 순 정렬
        if (categoryNum == 3) postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByCommentCountDesc(2, pageable);

        //  조회 수 순 정렬
        if (categoryNum == 4) postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByViewCountDesc(2, pageable);

        System.out.println("여기 들어옴?" + postList);

        return ResponseDto.success(sortCompletionCategory(postList));

    }

    // 완료된 움짤 페이지 목록 제시어 x 조회
    public ResponseDto<?> readCompletionTopicNull(Long categoryNum, int size, int page) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postList = null;

        // 최신 순 정렬
        if (categoryNum == 1) postList = postRepository.findAllByTopicIsNullAndStatusOrderByCompletAtDesc(2, pageable);

        // 좋아요 순 정렬
        if (categoryNum == 2) postList = postRepository.findAllByTopicIsNullAndStatusOrderByLikeCountDesc(2, pageable);

        // 댓글 수 순 정렬
        if (categoryNum == 3) postList = postRepository.findAllByTopicIsNullAndStatusOrderByCommentCountDesc(2, pageable);

        //  조회 수 순 정렬
        if (categoryNum == 4) postList = postRepository.findAllByTopicIsNullAndStatusOrderByViewCountDesc(2, pageable);

        return ResponseDto.success(sortCompletionCategory(postList));

    }

    // 완료된 움짤 카테고리 정렬 중복 매서드 처리
    public List<PostCompletionResponseDto> sortCompletionCategory(Page<Post> postList) {
        List<PostCompletionResponseDto> postCompletionResponseDtoList = new ArrayList<>();
        for (Post post : postList) {

            Long id = post.getId();
            String gifUrl = post.getGifUrl();
            int likeCount = post.getLikeCount();
            String topic = post.getTopic();
            String nickname = post.getMember().getNickname();
            String profileImg = post.getMember().getProfileImg();
            int commentCount = post.getCommentCount();
            int repotCount = post.getReportCount();

            // 완성된 날짜를 계산해야됨 -> 해당 게시글의 마지막 프레임이 생성된 시각
//            int postFrameTotal = post.getFrameTotal();
//            PostRelay postRelay = postRelayRepository.findByPostAndFrameNum(post, postFrameTotal); // 해당 게시물의 총 프레임 수 = 마지막 순번의 프레임 번호
//            LocalDateTime date = postRelay.getCreatedAt();
            LocalDateTime date = post.getModifiedAt();
            LocalDateTime completAt = post.getCompletAt();

            int viewCount = post.getViewCount();
            int status = post.getStatus(); // 반드시 2의 값을 가질것임

            List<Member> members = new ArrayList<>();

            // 참가자 수를 구할건데 게시물에 연관된 릴레이 테이블의 게시물을 모두 불러와서 프레임별로 멤버를 뽑아낼것임
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            for (PostRelay postRelays : postRelayList) {

                Member member = postRelays.getMember();
                members.remove(member);
                members.add(member);

            }

            // 참가자 중에서 게시물 작성자 제외하기
            Member member = post.getMember();
            members.remove(member);

            int participantCount = members.size();

            List<ParticipantResponseDto> participantResponseDtoList = new ArrayList<>();

            // 생성된 멤버 명단에서 하나씩 돌면서 멤버정보 세분화하여 뽑아내기
            for (Member memberList : members) {

                ParticipantResponseDto participantResponseDto = new ParticipantResponseDto(memberList.getNickname(), memberList.getProfileImg());
                participantResponseDtoList.add(participantResponseDto);

            }


            PostCompletionResponseDto postCompletionResponseDto = new PostCompletionResponseDto(id, gifUrl, likeCount, topic, nickname, profileImg, commentCount, repotCount, date, viewCount, status, participantResponseDtoList, participantCount, completAt);
            postCompletionResponseDtoList.add(postCompletionResponseDto);
        }

        return postCompletionResponseDtoList;
    }

    // 완료된 움짤 디테일 페이지 조회
    @Transactional
    public ResponseDto<?> readCompletionDetail(Long postid, boolean login) {

        Post post = postRepository.findById(postid).orElse(null);
        if(post == null) {
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }

        boolean liked = false;

        if (login) { // 로그인 했음

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDetails userDetails = (UserDetails)principal;

            Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (member == null) {
                ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);

            }

            // 좋아요 안한 게시물
            liked = postLikeRepository.existsByPostAndMember(post, member);

        }

        // 조회수 1 증가
        int viewCountCal = post.getViewCount() + 1;
        post.updateViewCount(viewCountCal);
        postRepository.save(post);

        Long id = post.getId();
        int frameTotal = post.getFrameTotal();
        String topic = post.getTopic();
        String gifUrl = post.getGifUrl();
        LocalDateTime createdAt = post.getCreatedAt();

        // 해당 게시물의 프레임 리스트 작성
        List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
        List<FrameImgListResponseDto> frameImgListResponseDtoList = new ArrayList<>();
        for (PostRelay postRelay : postRelayList) {

            String imgUrl = postRelay.getImgUrl();
            int frameNum = postRelay.getFrameNum();
            String nickname = postRelay.getMember().getNickname();
            String profileImg = postRelay.getMember().getProfileImg();

            FrameImgListResponseDto frameImgListResponseDto = new FrameImgListResponseDto(imgUrl, frameNum, nickname, profileImg);
            frameImgListResponseDtoList.add(frameImgListResponseDto);

        }

        int likeCount = post.getLikeCount();
        int viewCount = post.getViewCount();
        int reportCount = post.getReportCount();

        // 댓글 리스트 작성
        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentListResponseDto> commentListResponseDtoList = new ArrayList<>();
        for (Comment comment : commentList) {

            Long commentId = comment.getId();
            String profileImg = comment.getMember().getProfileImg();
            String nickname = comment.getMember().getNickname();
            String commentContent = comment.getComment();
            LocalDateTime commentCreatedAt = comment.getCreatedAt();

            CommentListResponseDto commentListResponseDto = new CommentListResponseDto(commentId, profileImg, nickname, commentContent, commentCreatedAt);
            commentListResponseDtoList.add(commentListResponseDto);

        }

        PostCompletionDetailResponseDto postCompletionDetailResponseDto = new PostCompletionDetailResponseDto(id, liked, frameTotal, topic, gifUrl, createdAt, frameImgListResponseDtoList, likeCount, viewCount, reportCount, commentListResponseDtoList);
        return ResponseDto.success(postCompletionDetailResponseDto);


    }


}
