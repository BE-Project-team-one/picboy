package com.sparta.picboy.configuration;

import com.sparta.picboy.jwt.JwtAccessDeniedHandler;
import com.sparta.picboy.jwt.JwtAuthenticationEntryPoint;
import com.sparta.picboy.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**");
    }


    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF protection 을 비활성화
//        http.cors();
        http.csrf().disable();

        http
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers("/user/**").permitAll() // 로그인,회원가입은 토큰없이도 가능
                        .antMatchers(HttpMethod.OPTIONS,"/**").permitAll() // 이거 혹시 될라나?
                        .antMatchers(HttpMethod.GET,"/post/**").permitAll()
                        .antMatchers("/socket/**").permitAll()
                        .antMatchers(HttpMethod.DELETE,"/post/**").permitAll()
                        .antMatchers(HttpMethod.GET,"/comment/**").permitAll()
                        .antMatchers(HttpMethod.GET,"/mypage/**").permitAll()
                        .antMatchers("/main/best-top10").permitAll()
//                        .antMatchers(HttpMethod.GET,"/oauth/**").permitAll() // 혹시 이거때문??
                        // 나머지 어떤 요청이든 '인증' 필요
                        .anyRequest().authenticated());

        http.headers()
                .frameOptions()
                .sameOrigin();


        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        http.apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}
