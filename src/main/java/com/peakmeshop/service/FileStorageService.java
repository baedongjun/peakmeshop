package com.peakmeshop.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file, String filePath) throws IOException;

    void deleteFile(String fileUrl);

    byte[] getFileContent(String fileUrl) throws IOException;

    String getFileUrl(String filePath);
}