package com.peakmeshop.domain.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.common.exception.FileStorageException;
import com.peakmeshop.domain.service.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final String fileBaseUrl;

    public FileStorageServiceImpl(
            @Value("${file.upload-dir:./uploads}") String uploadDir,
            @Value("${file.base-url:http://localhost/api/files}") String baseUrl) {

        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileBaseUrl = baseUrl;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String filePath) throws IOException {
        // 파일명 정규화
        String fileName = StringUtils.cleanPath(filePath);

        try {
            // 파일명에 부적절한 문자가 있는지 확인
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // 파일 저장 경로 생성
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            // 디렉토리가 없으면 생성
            Files.createDirectories(targetLocation.getParent());

            // 파일 저장
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return getFileUrl(fileName);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String fileName = getFileNameFromUrl(fileUrl);
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file. " + ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] getFileContent(String fileUrl) throws IOException {
        String fileName = getFileNameFromUrl(fileUrl);
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

        if (!Files.exists(filePath)) {
            throw new FileStorageException("File not found: " + fileName);
        }

        return Files.readAllBytes(filePath);
    }

    @Override
    public String getFileUrl(String filePath) {
        return fileBaseUrl + "/" + filePath;
    }

    private String getFileNameFromUrl(String fileUrl) {
        if (fileUrl.startsWith(fileBaseUrl)) {
            return fileUrl.substring(fileBaseUrl.length() + 1);
        }
        return fileUrl;
    }
}