package com.recipe.backend.global.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String errorMessage;

        // BadCredentialsException 또는 특정 예외를 통해 토큰 만료 여부를 확인
        if (authException instanceof BadCredentialsException) {
            errorMessage = "계정이 유효하지 않습니다. 다시 로그인해주세요.";
        } else{// 그 외의 인증 실패 처리 (예: 토큰 만료 등)
            errorMessage = "로그인이 필요합니다.";
        }

        // 클라이언트 측에서 alert 창을 띄우고 로그인 페이지로 리다이렉트하는 HTML/JavaScript 코드 작성
        response.setContentType("text/html;charset=UTF-8");
        String script = "<script type=\"text/javascript\">" +
                "alert('" + errorMessage + "');" +
                "window.location.href = '/users/login';" +
                "</script>";
        response.getWriter().write(script);
    }
}
