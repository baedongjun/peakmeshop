package com.peakmeshop.dto;

/**
 * 파일 업로드 응답 정보를 전송하기 위한 DTO
 */
public record FileResponseDTO(
        String fileName,
        String fileDownloadUri,
        String fileType,
        long size
) {}