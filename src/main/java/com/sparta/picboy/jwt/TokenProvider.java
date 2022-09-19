package com.sparta.picboy.jwt;

import com.sparta.picboy.domain.Authority;
import com.sparta.picboy.domain.RefreshToken;
import com.sparta.picboy.domain.user.Member;
import com.sparta.picboy.dto.request.TokenDto;
import com.sparta.picboy.repository.user.MemberRepository;
import com.sparta.picboy.repository.user.RefreshTokenRepository;
import com.sparta.picboy.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;           // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 2;  // 2시간

    private final Key key;
    private final MemberRepository memberRepository;

    public TokenProvider(@Value("${jwt.secret}") String secretKey, UserDetailsServiceImpl userDetailsService, RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository) {
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
    }


    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String accessToken, String refreshToken, HttpServletResponse httpServletResponse) { // 해당 함수 호출 시, 제공된 토큰의 유효성을 검사해서 true,false 리턴해줌
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken); // 토큰 디코드
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");

            // 만료된건 알겠고, 우선 만료된 토큰에서 유저 이름빼오자
            Authentication authentication = getAuthentication(accessToken);
            String username = authentication.getName();
            Member member = memberRepository.findByUsername(username).orElse(null);
            if (member == null) { // 토큰에 저장된 유저네임은 db에 존재하지 않는데?
                return false;
            }

            // 위에서 찾은 유저네임으로 리프레쉬 토큰이 존재하는지 확인
            RefreshToken refreshTokens = refreshTokenRepository.findByMember_Username(username);

            // refreshTokens 유효성 검사
            if (!refreshTokenValidation(refreshToken)) {
                return false;
            }

            // 헤더에 있는 리프레쉬 토큰과 DB에 저장된 리프레쉬 토큰이 같은지 확인
            if (!refreshTokens.getValue().equals(refreshToken)) {
                return false;
            }

            TokenDto tokenDto = accessTokenReissuance(member);
            httpServletResponse.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
            httpServletResponse.addHeader("Refresh-Token", refreshToken);
            httpServletResponse.addHeader("AccessTokenExpiredTime", String.valueOf(tokenDto.getAccessTokenExpiresIn()));

            log.info("토큰이 재발급 되었습니다.");
            return true;

        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    public boolean validateToken(String accessToken) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken); // 토큰 디코드
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;

    }

    // refreshToken 유효성 검사 매서드
    public boolean refreshTokenValidation(String refreshToken) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken); // 토큰 디코드
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 리프레쉬 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 리프레쉬 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 리프레쉬 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 리프레쉬 토큰이 잘못되었습니다.");
        }
        return false;

    }

    // AccessToken 재발급 매서드
    public TokenDto accessTokenReissuance(Member member) {

        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim(AUTHORITIES_KEY, Authority.ROLE_USER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();

    }


    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim(AUTHORITIES_KEY, Authority.ROLE_USER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(member.getId())
                .member(member)
                .value(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
     }
}
