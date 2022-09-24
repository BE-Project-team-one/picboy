package com.sparta.picboy.repository.queryDsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.domain.user.QMember;
import com.sparta.picboy.dto.response.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserResponseDto> findAllMember() {
        QMember member = QMember.member;

        List<Member> membersList = queryFactory.selectFrom(member)
                .fetch();

        List<UserResponseDto> responseList = new ArrayList<>();
        for(Member m : membersList) {
            responseList.add(
                    new UserResponseDto(
                            m.getId(),
                            m.getUsername(),
                            m.getNickname(),
                            m.getProfileImg(),
                            m.getStatus(),
                            m.getPhoneNumber(),
                            m.getAuthority(),
                            m.getKakaoId()
                    ));
        }

        return responseList;
    }

    @Override
    public void userLock(Long memberId) {
        QMember member = QMember.member;

        queryFactory.update(member)
                .where(member.id.eq(memberId))
                .set(member.status, 3)
                .execute();
    }

    @Override
    public void userClear(Long memberId) {
        QMember member = QMember.member;

        Member member1 = queryFactory.selectFrom(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        if(member1.getKakaoId() == null) {
            queryFactory.update(member)
                    .where(member.id.eq(memberId))
                    .set(member.status, 1)
                    .execute();
        } else {
            queryFactory.update(member)
                    .where(member.id.eq(memberId))
                    .set(member.status, 2)
                    .execute();
        }
    }
}
