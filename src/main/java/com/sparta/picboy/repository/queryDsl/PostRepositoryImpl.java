package com.sparta.picboy.repository.queryDsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.picboy.domain.post.*;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.domain.user.QMember;
import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import com.sparta.picboy.dto.response.post.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Querydsl를 이용한 쿼리를 작성한다.
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    //카테고리 정렬 메소드
    @Override
    public Page<MypageResponseDto> categorySort(String username, int tabNum, int categoryNum, Pageable pageable) {
        //Q클래스를 이용한다.
        QPost post = QPost.post;
        QPostRelay postRelay = QPostRelay.postRelay;
        QMember member = QMember.member;
        QHidePost hidePost = QHidePost.hidePost;
        QLikes likes = QLikes.likes;
        QReport report = QReport.report;

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder builder2 = new BooleanBuilder();


        if (tabNum != 3) {
            // 내가 참여한 전체 게시물물
            if (tabNum == 0) builder.and(null);
            // 내가 작성한 게시물
            if (tabNum == 1) builder.and(post.member.username.eq(username));
            // 내가 참여한 게시물
            if (tabNum == 2) builder.and(post.member.username.ne(username));

            builder2.and(
                    post.id.notIn(queryFactory.select(hidePost.post.id)
                            .from(hidePost)
                            .innerJoin(hidePost.member, member)
                            .where(hidePost.member.username.eq(username))
                            .fetch())
            );
        } else {
            // 내가 숨긴 게시물
            builder.and(
                    post.id.in(queryFactory.select(hidePost.post.id)
                            .from(hidePost)
                            .innerJoin(hidePost.member, member)
                            .where(hidePost.member.username.eq(username))
                            .fetch())
            );
            builder2.and(null);
        }

        List<PostRelay> postRelayList = queryFactory.select(postRelay)
                .from(postRelay)
                .innerJoin(postRelay.post, post)
                .innerJoin(post.member, member)
                .innerJoin(postRelay.member, member)
                .where(
                        postRelay.member.username.eq(username)
                                .and(post.status.in(1, 2))
                                .and(builder)
                                .and(builder2)

                )
                .groupBy(postRelay.post)
                .orderBy(getOrder(categoryNum), getOrder(1))
                .fetch();

        // list의 size를 얻어 totalElements와 totalPages에 사용
        int total = postRelayList.size();
        // list의 offset부터 limit까지 가져와야 하기 때문에 하기와 같이 구현
        int limit = pageable.getPageSize() * (pageable.getPageNumber() + 1);
        List<MypageResponseDto> responseDtoList = new ArrayList<>();

        // 기존에 list로 가져온 응답 중, 필요한 offset ~ limit까지의 데이터만 담음
        for (int i = pageable.getPageNumber() * pageable.getPageSize(); i < limit && i < total; i++) {
            Post resultPost = queryFactory.selectFrom(post)
                    .where(post.id.eq(postRelayList.get(i).getPost().getId()))
                    .fetchOne();

            // 게시물 참여자 수 구하기 (중복 제거)
            int count = queryFactory.select(postRelay)
                    .from(postRelay)
                    .innerJoin(postRelay.post, post)
                    .where(postRelay.post.id.eq(postRelayList.get(i).getPost().getId()))
                    .groupBy(postRelay.post.id, postRelay.member.id)
                    .fetch().size();

            // 내가 좋아요 누른 게시물 찾기
            Likes resultLike = queryFactory.selectFrom(likes)
                    .where(
                            likes.post.eq(resultPost)
                                    .and(likes.member.username.eq(username))
                    ).fetchOne();

            boolean likesFlag = resultLike != null;

            // 내가 신고한 게시글
            Report resultReport = queryFactory.selectFrom(report)
                    .where(
                            report.post.eq(resultPost)
                                    .and(report.member.username.eq(username))
                    ).fetchOne();

            boolean reportFlag = resultReport != null;


            responseDtoList.add(new MypageResponseDto(
                    resultPost.getId(),
                    resultPost.getImgUrl(),
                    resultPost.getGifUrl(), // gif
                    resultPost.getLikeCount(),
                    resultPost.getCommentCount(),
                    resultPost.getViewCount(),
                    resultPost.getReportCount(),
                    resultPost.getTopic(),
                    resultPost.getMember().getNickname(),
                    resultPost.getMember().getProfileImg(),
                    count - 1, // 글쓴이 외 참여자 수
                    resultPost.getExpiredAt(),
                    resultPost.getStatus(),
                    likesFlag,
                    reportFlag
            ));
        }
        return new PageImpl<>(responseDtoList, pageable, total);

    }

    // 게시글 전체 조회
    @Override
    public List<PostResponseDto> findAllPost() {
        QPost post = QPost.post;

        List<Post> postList = queryFactory.selectFrom(post).fetch();
        List<PostResponseDto> dto = new ArrayList<>();
        for (Post p : postList) {
            dto.add(new PostResponseDto(
                    p.getId(),
                    p.getTopic(),
                    p.getFrameNum(),
                    p.getFrameTotal(),
                    p.getImgUrl(),
                    p.getExpiredAt(),
                    p.getStatus(),
                    p.getGifUrl(),
                    p.getCommentCount(),
                    p.getLikeCount(),
                    p.getViewCount(),
                    p.getReportCount(),
                    p.getMember()
            ));
        }
        return dto;
    }

    // 알람 읽음 처리 갯수 확인
    @Override
    public int readCheckPost(String username) {
        QAlert alert = QAlert.alert;

        List<Alert> alertList = queryFactory.select(alert)
                .from(alert)
                .where(
                        alert.member.username.eq(username)
                                .and(alert.flag.eq(false))
                )
                .fetch();

        return alertList.size();
    }

    // 알람 전체 읽음 처리로 update
    @Override
    public void alertAllRead(String username) {
        QMember member = QMember.member;
        QAlert alert = QAlert.alert;

        Member member1 = queryFactory.selectFrom(member)
                .where(member.username.eq(username))
                .fetchOne();

        queryFactory.update(alert)
                .where(alert.member.eq(member1))
                .set(alert.flag, true)
                .execute();
    }

    // 내 알람 전체 가져오기
    @Override
    public List<AlertInboxResponseDto> alertAllGet(String username) {
        QAlert alert = QAlert.alert;

        List<Alert> alertList = queryFactory.selectFrom(alert)
                .where(alert.member.username.eq(username))
                .orderBy(alert.createdAt.desc())
                .fetch();

        List<AlertInboxResponseDto> dtoList = new ArrayList<>();
        for (Alert a : alertList) {


            dtoList.add(new AlertInboxResponseDto(
                    a.getPost().getId(),
                    a.getContent(),
                    a.getMember().getNickname(),
                    a.getCreatedAt(),
                    a.isFlag(),
                    a.getPost().getTopic()
            ));
        }
        return dtoList;
    }

    // 동적 정렬 기준 메소드
    private OrderSpecifier<?> getOrder(int categoryNum) {
        QPost post = QPost.post;
        if (categoryNum == 1) return post.createdAt.desc();
        if (categoryNum == 2) return post.likeCount.desc();
        if (categoryNum == 3) return post.commentCount.desc();
        if (categoryNum == 4) return post.viewCount.desc();
        return null;
    }

    // 완료된 페이지 공통 조회
    @Override
    public Page<PostCompletionResponseDto> postRead(int tabNum, int categoryNum, Pageable pageable) {
        // tabNum : 0->전체 1->제시어o 2->제시어x
        // categoryNum : 1->최신순 2->좋아요순 3->댓글순 4->조회순

        //Q클래스를 이용한다.
        QPost post = QPost.post;
        QPostRelay postRelay = QPostRelay.postRelay;

        BooleanBuilder builder = new BooleanBuilder();

        // 전체 게시물물
        if (tabNum == 0) builder.and(null);
        // 제시어 o 게시물
        if (tabNum == 1) builder.and(post.topic.isNotNull());
        // 제시어 x 게시물
        if (tabNum == 2) builder.and(post.topic.isNull());


        List<PostRelay> postRelayList = queryFactory.selectFrom(postRelay)
                .innerJoin(postRelay.post, post)
                .where(post.status.eq(2)
                        .and(postRelay.frameNum.eq(post.frameTotal))
                        .and(builder))
                .orderBy(order(categoryNum))
                .fetch();

        List<PostCompletionResponseDto> postCompletionResponseDtoList = new ArrayList<>();

        // list의 size를 얻어 totalElements와 totalPages에 사용
        int total = postRelayList.size();
        // list의 offset부터 limit까지 가져와야 하기 때문에 하기와 같이 구현
        int limit = pageable.getPageSize() * (pageable.getPageNumber() + 1);

        // 기존에 list로 가져온 응답 중, 필요한 offset ~ limit까지의 데이터만 담음
        for (int i = pageable.getPageNumber() * pageable.getPageSize(); i < limit && i < total; i++) {
            Post resultPost = queryFactory.selectFrom(post)
                    .where(post.id.eq(postRelayList.get(i).getPost().getId()))
                    .fetchOne();

            Long id = resultPost.getId();
            String gifUrl = resultPost.getGifUrl();
            int likeCount = resultPost.getLikeCount();
            String topic = resultPost.getTopic();
            Long memberid = resultPost.getMember().getId();
            String nickname = resultPost.getMember().getNickname();
            String profileImg = resultPost.getMember().getProfileImg();
            int commetCount = resultPost.getCommentCount();
            int reportCount = resultPost.getReportCount();
            LocalDateTime date = postRelayList.get(i).getCreatedAt(); // 마지막 프레임 생성일자 = gif 게시물 생성일
            int viewCount = resultPost.getViewCount();
            int status = resultPost.getStatus();

            // 게시글을 만드는데 참여한 인원 구하기
            List<Member> members = new ArrayList<>();
            List<ParticipantResponseDto> participantResponseDtoList = new ArrayList<>();
            List<PostRelay> postRelayListForMember = queryFactory.selectFrom(postRelay)
                    .innerJoin(postRelay.post, post)
                    .where(post.eq(resultPost))
                    .fetch();

            // 중복제거 하면서 멤버 추가
            for (PostRelay forFor : postRelayListForMember) {
                members.remove(forFor.getMember());
                members.add(forFor.getMember());
            }

            // 참가자 중에서 작성자 제외하기
            members.remove(resultPost.getMember());

            // 리스폰스 작성
            for (Member members2 : members) {
                participantResponseDtoList.add(new ParticipantResponseDto(members2.getId(),members2.getUsername(),members2.getNickname(), members2.getProfileImg()));
            }

            int participantCount = participantResponseDtoList.size();

            PostCompletionResponseDto postCompletionResponseDto = new PostCompletionResponseDto(id, gifUrl, likeCount, topic, memberid, nickname, profileImg, commetCount, reportCount, date, viewCount, status, participantResponseDtoList, participantCount);
            postCompletionResponseDtoList.add(postCompletionResponseDto);

        }

        return new PageImpl<>(postCompletionResponseDtoList, pageable, total);

    }

    @Override
    public int topicIsNotNullPost() {
        QPost post = QPost.post;

        return queryFactory.selectFrom(post)
                .where(post.topic.isNotNull())
                .fetch().size();
    }

    @Override
    public int topicIsNullPost() {
        QPost post = QPost.post;

        return queryFactory.selectFrom(post)
                .where(post.topic.isNull())
                .fetch().size();
    }

    @Override
    public int completePost() {
        QPost post = QPost.post;

        return queryFactory.selectFrom(post)
                .where(post.frameTotal.eq(post.frameNum))
                .fetch().size();
    }

    @Override
    public int proceedingPost() {
        QPost post = QPost.post;

        return queryFactory.selectFrom(post)
                .where(post.frameTotal.ne(post.frameNum))
                .fetch().size();
    }

    @Override
    public int hidnPost() {
        QPost post = QPost.post;

        return queryFactory.selectFrom(post)
                .where(post.status.eq(3))
                .fetch().size();
    }

    @Override
    public int todayCreatePost() {
        QPost post = QPost.post;
        LocalDate now = LocalDate.now();

        StringTemplate dateFormat = Expressions.stringTemplate(
                "DATE_FORMAT( {0}, {1} )",
                post.createdAt,
                ConstantImpl.create("%Y-%m-%d"));

        return  queryFactory.selectFrom(post)
                .where(dateFormat.eq(now.toString()))
                .fetch().size();
    }

    @Override
    public int todayCompletePost() {
        QPost post = QPost.post;
        QPostRelay postRelay = QPostRelay.postRelay;
        LocalDate now = LocalDate.now();

        StringTemplate dateFormat = Expressions.stringTemplate(
                "DATE_FORMAT( {0}, {1} )",
                postRelay.createdAt,
                ConstantImpl.create("%Y-%m-%d"));

        return  queryFactory.selectFrom(postRelay)
                .innerJoin(postRelay.post, post)
                .where(
                        post.frameTotal.eq(postRelay.frameNum)
                                .and(dateFormat.eq(now.toString()))
                ).fetch().size();
    }

    @Override
    public int todayDeletePost() {
        QPost post = QPost.post;
        LocalDate now = LocalDate.now();

        StringTemplate dateFormat = Expressions.stringTemplate(
                "DATE_FORMAT( {0}, {1} )",
                post.expiredAt,
                ConstantImpl.create("%Y-%m-%d"));

        return  queryFactory.selectFrom(post)
                .where(dateFormat.eq(now.toString())
                        .and(post.status.in(1))
                )
                .fetch().size();
    }

    private OrderSpecifier<?> order(int categoryNum) {
        QPost post = QPost.post;
        QPostRelay postRelay = QPostRelay.postRelay;
        if(categoryNum == 1) return postRelay.createdAt.desc();
        if(categoryNum == 2) return post.likeCount.desc();
        if(categoryNum == 3) return post.commentCount.desc();
        if(categoryNum == 4) return post.viewCount.desc();
        return null;
    }
}
