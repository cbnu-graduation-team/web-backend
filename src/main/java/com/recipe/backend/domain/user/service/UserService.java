package com.recipe.backend.domain.user.service;

import com.recipe.backend.domain.user.dto.SignupRequest;
import org.springframework.validation.BindingResult;

public interface UserService {
    // 회원가입
    void signup(SignupRequest signupRequest, BindingResult bindingResult);

    //
}
