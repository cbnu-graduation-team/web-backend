package com.recipe.backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotNull
    @Size(min = 3, max = 30, message = "아이디는 3글자 이상 30글자 미만이여야 합니다.")
    private String username;
    @NotNull
    @Size(min = 8, max = 100, message = "비밀번호는 8글자 이상 20글자 미만이여야 합니다.")
    private String password;
    @NotNull
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;
}
