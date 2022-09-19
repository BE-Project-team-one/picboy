package com.sparta.picboy.repository.user;

import com.sparta.picboy.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByUsername(String username); // 유저네임으로 찾아오는 메소드


}

