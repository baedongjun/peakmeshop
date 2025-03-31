package com.peakmeshop.domain.service;

import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.api.dto.FileDTO;

public interface FileService {

    FileDTO uploadFile(MultipartFile file, String directory);

    void deleteFile(String fileUrl);

    byte[] getFileContent(String fileUrl);
}