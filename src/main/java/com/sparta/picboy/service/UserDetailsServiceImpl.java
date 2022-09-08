package com.sparta.picboy.service;

import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.domain.UserDetailsImpl;
import com.sparta.picboy.repository.user.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
   // Optional<Member> member = memberRepository.findByNickname(username);
    Optional<Member> member = memberRepository.findByUsername(username);
    return member
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
  }
}
