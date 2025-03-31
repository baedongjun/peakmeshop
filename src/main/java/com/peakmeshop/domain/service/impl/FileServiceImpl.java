package com.peakmeshop.domain.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.api.dto.FileDTO;
import com.peakmeshop.common.exception.FileStorageException;
import com.peakmeshop.domain.service.FileService;
import com.peakmeshop.domain.service.FileStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStorageService fileStorageService;

    @Override
    public FileDTO uploadFile(MultipartFile file, String directory) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String filePath = directory + "/" + fileName;

            String fileUrl = fileStorageService.storeFile(file, filePath);

            return FileDTO.builder()
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .build();
        } catch (Exception e) {
            throw new FileStorageException("Failed to upload file", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        fileStorageService.deleteFile(fileUrl);
    }

    @Override
    public byte[] getFileContent(String fileUrl) {
        try {
            return fileStorageService.getFileContent(fileUrl);
        } catch (IOException e) {
            throw new FileStorageException("Failed to get file content", e);
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}