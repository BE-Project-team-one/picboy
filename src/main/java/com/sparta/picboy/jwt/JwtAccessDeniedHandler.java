package com.sparta.picboy.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.picboy.dto.ResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요한 권한이 없이 접근하려 할때 403
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(
                new ObjectMapper().writeValueAsString(
                        ResponseDto.fail("FORBIDDEN", "로그인이 필요합니다.")
                )
        );
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}

