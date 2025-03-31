package com.peakmeshop.api.dto;

import java.time.LocalDate;

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
public class ProfileUpdateDTO {

    @NotBlank(message = "이름은 필수 입력 항목입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    private String name;

    @Size(max = 20, message = "전화번호는 20자 이하로 입력해주세요")
    private String phone;

    private LocalDate birthDate;

    @Size(max = 10, message = "성별은 10자 이하로 입력해주세요")
    private String gender;
}