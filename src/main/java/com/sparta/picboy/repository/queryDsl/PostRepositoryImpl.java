package com.sparta.picboy.repository.queryDsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.picboy.domain.post.*;
import com.sparta.picboy.domain.user.QMember;
import com.sparta.picboy.dto.response.mypage.MypageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;

/**
 * Querydsl를 이용한 쿼리를 작성한다.
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    //카테고리 정렬 메소드
    @Override
    public Page<MypageResponseDto> categorySort(String username,int tabNum, int categoryNum, Pageable pageable) {
        //Q클래스를 이용한다.
        QPost post = QPost.post;
        QPostRelay postRelay = QPostRelay.postRelay;
        QMember member = QMember.member;
        QHidePost hidePost = QHidePost.hidePost;

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder builder2 = new BooleanBuilder();


       if(tabNum != 3) {
           // 내가 참여한 전체 게시물물
           if(tabNum == 0) builder.and(null);
           // 내가 작성한 게시물
           if(tabNum == 1) builder.and(post.member.username.eq(username));
           // 내가 참여한 게시물
           if(tabNum == 2) builder.and(post.member.username.ne(username));

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
                        .and(post.status.in(1,2))
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
        for (int i = pageable.getPageNumber() * pageable.getPageSize(); i < limit && i < total; i ++) {
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
                    resultPost.getStatus()
            ));
        }
        return  new PageImpl<>(responseDtoList, pageable, total);

    }

    // 동적 정렬 기준 메소드
    private OrderSpecifier<?> getOrder(int categoryNum) {
        QPost post = QPost.post;
        if(categoryNum == 1) return post.createdAt.desc();
        if(categoryNum == 2) return post.likeCount.desc();
        if(categoryNum == 3) return post.commentCount.desc();
        if(categoryNum == 4) return post.viewCount.desc();
        return null;
    }


}
