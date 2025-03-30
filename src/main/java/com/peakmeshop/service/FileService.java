package com.peakmeshop.service;

import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.dto.FileDTO;

public interface FileService {

    FileDTO uploadFile(MultipartFile file, String directory);

    void deleteFile(String fileUrl);

    byte[] getFileContent(String fileUrl);
}