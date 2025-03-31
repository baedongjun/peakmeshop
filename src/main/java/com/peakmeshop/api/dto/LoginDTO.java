package com.peakmeshop.api.dto;

import com.peakmeshop.api.dto.LoginRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    private boolean rememberMe;
    public LoginRequest toLoginRequestDTO() {
        return LoginRequest.builder()
                .userId(this.userId)
                .password(this.password)
                .rememberMe(this.rememberMe)
                .build();
    }
}