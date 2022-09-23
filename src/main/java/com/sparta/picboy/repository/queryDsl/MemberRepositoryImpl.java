package com.sparta.picboy.repository.queryDsl;

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
}
