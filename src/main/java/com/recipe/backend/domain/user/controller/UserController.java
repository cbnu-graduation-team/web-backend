package com.recipe.backend.domain.user.controller;

import com.recipe.backend.domain.inventory.repository.InventoryRepository;
import com.recipe.backend.domain.user.dto.LoginRequest;
import com.recipe.backend.domain.user.dto.LoginResponse;
import com.recipe.backend.domain.user.dto.SignupRequest;
import com.recipe.backend.domain.user.service.UserService;
import com.recipe.backend.global.config.auth.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager am;
    private final JwtUtil jwtUtil;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String join(@Valid SignupRequest dto, BindingResult bindingResult, Model model) {
        userService.signup(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        // 회원가입이 성공한 경우 플래그를 설정
        model.addAttribute("success", true);

        // 회원가입 성공 후 signup 페이지를 다시 렌더링
        return "user/signup";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "user/login"; // login.html을 렌더링
    }

    @PostMapping("/login")
    public String authenticateUser(@ModelAttribute LoginRequest loginRequest, Model model, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = am.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication.getName());

            redirectAttributes.addFlashAttribute("loginResponse", new LoginResponse(loginRequest.getUsername(), jwt));
            return "redirect:/"; // 로그인 성공 후 이동할 페이지
        } catch (BadCredentialsException e) {
            model.addAttribute("errorMessage", "아이디 혹은 비밀번호가 틀렸습니다.");
            return "user/login"; // 로그인 실패 시 다시 login 페이지를 렌더링
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // 현재 사용자 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // SecurityContextHolder에서 인증 정보 제거
        SecurityContextHolder.clearContext();

        // 로컬 스토리지에 저장된 JWT 토큰 삭제를 위한 플래그 설정 (뷰에서 처리)
        redirectAttributes.addFlashAttribute("logoutSuccess", true);

        return "redirect:/";
    }
}
