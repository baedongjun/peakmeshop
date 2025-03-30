package com.peakmeshop.dto;

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
public class PasswordChangeDTO {

    @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하로 입력해주세요")
    private String newPassword;
}