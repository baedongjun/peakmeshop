package com.peakmeshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 이메일 템플릿 정보를 전송하기 위한 DTO
 */
public record EmailTemplateDTO(
        Long id,

        @NotBlank(message = "템플릿 코드는 필수입니다")
        @Size(max = 50, message = "템플릿 코드는 50자 이내여야 합니다")
        String code,

        @NotBlank(message = "템플릿 이름은 필수입니다")
        @Size(max = 100, message = "템플릿 이름은 100자 이내여야 합니다")
        String name,

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이내여야 합니다")
        String subject,

        @NotBlank(message = "내용은 필수입니다")
        String content,

        String description,

        Boolean isActive
) {}