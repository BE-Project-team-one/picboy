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
import com.sparta.picboy.repository.post.PostRelayRepository;
import com.sparta.picboy.repository.post.PostRepository;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReadService {

    private final PostRepository postRepository;
    private final PostRelayRepository postRelayRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;


    // 메인페이지 베스트 움짤 Top 10
    public ResponseDto<?> mainTop10() {
        List<Post> postListTop10 = postRepository.findTop10ByStatusOrderByLikeCountDesc(2);
        List<PostMainTop10ResponseDto> postMainTop10ResponseDtoList = new ArrayList<>();
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
                memberList.remove(postRelay.getMember());
                memberList.add(postRelay.getMember());

            }


            // 리스폰스는 '작성자 외 n 명' 이라서 작성자는 따로 빼고 연산
            memberList.remove(post.getMember());
            int memberCount = memberList.size();

            PostMainTop10ResponseDto postMainTop10ResponseDto = new PostMainTop10ResponseDto(id, gifUrl, likeCount, topic, nickname, memberCount);
            postMainTop10ResponseDtoList.add(postMainTop10ResponseDto);

        }

        return ResponseDto.success(postMainTop10ResponseDtoList);

    }

    // 로그인 유저 정보 가져오기 <- 병합 후 유저 서비스에 옮겨놓기
    public ResponseDto<?> loginUserInfo(UserDetailsImpl userDetails) {
        Optional<Member> member = memberRepository.findByUsername(userDetails.getUsername());

        // 그럴 일이 없겠지만 혹여나 로그인한 유저의 정보를 못가져 왔을때를 위한 오류
        if (member.isEmpty()) {
            return ResponseDto.fail(ErrorCode.NOT_FOUND_MEMBER);
        }

        String username = member.get().getUsername();
        String nickname = member.get().getNickname();
        String profileImg = member.get().getProfileImg();
        String authority = member.get().getAuthority();

        MemberLoginInfoResponseDto memberLoginInfoResponseDto = new MemberLoginInfoResponseDto(username, nickname, profileImg, authority);
        return ResponseDto.success(memberLoginInfoResponseDto);


    }






    // 진행중인 움짤 페이지 목록 조회
    public ResponseDto<?> readProceeding(Long tabNum, int size, int page) {

        if (tabNum == 0) { // 게시글 전체

            String sortAt = "createdAt";
            Sort.Direction direction = Sort.Direction.DESC;
            Sort sort = Sort.by(direction, sortAt);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Post> postList = postRepository.findAllByStatus(1,pageable);
            return ResponseDto.success(sortProceedingCategory(postList));
        } else if (tabNum == 1) { // 제시어 o

            String sortAt = "createdAt";
            Sort.Direction direction = Sort.Direction.DESC;
            Sort sort = Sort.by(direction, sortAt);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Post> postList = postRepository.findAllByTopicIsNotNullAndStatus(1, pageable);
            return ResponseDto.success(sortProceedingCategory(postList));
        } else { // tabNum == 2 제시어 x

            String sortAt = "createdAt";
            Sort.Direction direction = Sort.Direction.DESC;
            Sort sort = Sort.by(direction, sortAt);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Post> postList = postRepository.findAllByTopicIsNullAndStatus(1, pageable);
            return ResponseDto.success(sortProceedingCategory(postList));
        }

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

            List<Member> members = new ArrayList<>();

            // 참가자 수를 구할건데 게시물에 연관된 릴레이 테이블의 게시물을 모두 불러와서 프레임별로 멤버를 뽑아낼것임
            List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
            for (PostRelay postRelay : postRelayList) {

                Member member = postRelay.getMember();
                members.remove(member);
                members.add(member);

            }

            int participantCount = members.size();

            List<ParticipantResponseDto> participantResponseDtoList = new ArrayList<>();

            // 생성된 멤버 명단에서 하나씩 돌면서 멤버정보 세분화하여 뽑아내기
            for (Member memberList : members) {

                String usernames = memberList.getUsername();
                String nicknames = memberList.getNickname();
                String profileImgs = memberList.getProfileImg();

                ParticipantResponseDto participantResponseDto = new ParticipantResponseDto(usernames, nicknames, profileImgs);
                participantResponseDtoList.add(participantResponseDto);

            }

            PostProceedingResponseDto postProceedingResponseDto = new PostProceedingResponseDto(id, imgUrl, topic, nickname, status, profileImg, participantResponseDtoList, participantCount);
            postProceedingResponseDtoList.add(postProceedingResponseDto);

        }

        return postProceedingResponseDtoList;

    }

    // 진행중인 움짤 디테일 페이지 조회
    public ResponseDto<?> readProceedingDetail(Long postid) {

        Optional<Post> postCheck = postRepository.findById(postid);
        if(postCheck.isEmpty()) { // 찾는 게시물이 존재하지 않을때
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }
        Post post = postRepository.findById(postid).orElseThrow();


        Long id = post.getId();
        int frameTotal = post.getFrameTotal();
        int frameNum = post.getFrameNum();
        String topic = post.getTopic();
        String imgUrl = post.getImgUrl();
        String nickname = post.getMember().getNickname();
        String profileImg = post.getMember().getProfileImg();
        LocalDateTime expiredAt = post.getExpiredAt();
        LocalDateTime createdAt = post.getCreatedAt();

        // 프레임 리스트 작성
        List<PostRelay> postRelayList = postRelayRepository.findAllByPost(post);
        List<FrameImgListResponseDto> frameImgListResponseDtoList = new ArrayList<>();
        for (PostRelay postRelay : postRelayList) {

            String relayImgUrl = postRelay.getImgUrl();
            int relayFrameNum = postRelay.getFrameNum();
            String relayNickname = postRelay.getMember().getNickname();
            String relayProfileImg = postRelay.getMember().getProfileImg();

            FrameImgListResponseDto frameImgListResponseDto = new FrameImgListResponseDto(relayImgUrl, relayFrameNum, relayNickname, relayProfileImg);
            frameImgListResponseDtoList.add(frameImgListResponseDto);
        }

        PostProceedingDetailResponseDto postProceedingDetailResponseDto = new PostProceedingDetailResponseDto(id, frameTotal, frameNum, topic, imgUrl, nickname, profileImg, expiredAt, createdAt, frameImgListResponseDtoList);
        return ResponseDto.success(postProceedingDetailResponseDto);
    }






    // 완료된 움짤 페이지 목록 전체 조회
    public ResponseDto<?> readCompletion(Long categoryNum, int size, int page) {

        if (categoryNum == 1) { // 최신 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByStatusOrderByCreatedAtDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else if (categoryNum == 2) { // 좋아요 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByStatusOrderByLikeCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else if (categoryNum == 3) { // 댓글 수 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByStatusOrderByCommentCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else { // categoryNum == 4 조회 수 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByStatusOrderByViewCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        }

    }

    // 완료된 움짤 페이지 목록 제시어 o 조회
    public ResponseDto<?> readCompletionTopicOk(Long categoryNum, int size, int page) {

        if (categoryNum == 1) { // 최신 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByCreatedAtDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else if (categoryNum == 2) { // 좋아요 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByLikeCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else if (categoryNum == 3) { // 댓글 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByCommentCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else { // categoryNum == 4 조회 수 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNotNullAndStatusOrderByViewCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        }

    }

    // 완료된 움짤 페이지 목록 제시어 x 조회
    public ResponseDto<?> readCompletionTopicNull(Long categoryNum, int size, int page) {

        if (categoryNum == 1) { // 최신 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNullAndStatusOrderByCreatedAtDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else if (categoryNum == 2) { // 좋아요 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNullAndStatusOrderByLikeCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else if (categoryNum == 3) { // 댓글 수 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNullAndStatusOrderByCommentCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        } else { // categoryNum == 4 조회 수 순 정렬

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByTopicIsNullAndStatusOrderByViewCountDesc(2, pageable);
            return ResponseDto.success(sortCompletionCategory(postList));
        }

    }

    // 완료된 움짤 카테고리 정렬 중복 매서드 처리
    public List<PostCompletionResponseDto> sortCompletionCategory(Page<Post> postList) {
        List<PostCompletionResponseDto> postCompletionResponseDtoList = new ArrayList<>();
        for (Post post : postList) {

            Long id = post.getId();
            String imgUrl = post.getGifUrl();
            int likeCount = post.getLikeCount();
            String topic = post.getTopic();
            String nickname = post.getMember().getNickname();
            int commentCount = post.getCommentCount();
            int repotCount = post.getReportCount();

            // 완성된 날짜를 계산해야됨 -> 해당 게시글의 마지막 프레임이 생성된 시각
            int postFrameTotal = post.getFrameTotal();
            PostRelay postRelay = postRelayRepository.findByPostAndFrameNum(post, postFrameTotal); // 해당 게시물의 총 프레임 수 = 마지막 순번의 프레임 번호
            LocalDateTime date = postRelay.getCreatedAt();

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

            int participantCount = members.size();

            List<ParticipantResponseDto> participantResponseDtoList = new ArrayList<>();

            // 생성된 멤버 명단에서 하나씩 돌면서 멤버정보 세분화하여 뽑아내기
            for (Member memberList : members) {

                String usernames = memberList.getUsername();
                String nicknames = memberList.getNickname();
                String profileImgs = memberList.getProfileImg();

                ParticipantResponseDto participantResponseDto = new ParticipantResponseDto(usernames, nicknames, profileImgs);
                participantResponseDtoList.add(participantResponseDto);

            }


            PostCompletionResponseDto postCompletionResponseDto = new PostCompletionResponseDto(id, imgUrl, likeCount, topic, nickname, commentCount, repotCount, date, viewCount, status, participantResponseDtoList, participantCount);
            postCompletionResponseDtoList.add(postCompletionResponseDto);
        }

        return postCompletionResponseDtoList;
    }

    // 완료된 움짤 디테일 페이지 조회
    @Transactional
    public ResponseDto<?> readCompletionDetail(Long postid) {

        Optional<Post> postCheck = postRepository.findById(postid);
        if (postCheck.isEmpty()) { // 존재하지 않는 게시물을 요청했을 때
            return ResponseDto.fail(ErrorCode.NOT_FOUNT_POST);
        }
        Post post = postRepository.findById(postid).orElseThrow();

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

        PostCompletionDetailResponseDto postCompletionDetailResponseDto = new PostCompletionDetailResponseDto(id, frameTotal, topic, gifUrl, createdAt, frameImgListResponseDtoList, likeCount, viewCount, commentListResponseDtoList);
        return ResponseDto.success(postCompletionDetailResponseDto);


    }

}
