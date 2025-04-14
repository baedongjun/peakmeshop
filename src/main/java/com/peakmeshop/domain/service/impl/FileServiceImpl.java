package com.peakmeshop.domain.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.api.dto.FileDTO;
import com.peakmeshop.domain.entity.File;
import com.peakmeshop.domain.repository.FileRepository;
import com.peakmeshop.domain.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final String uploadDir = "uploads";

    @Override
    @Transactional
    public FileDTO uploadFile(MultipartFile file, String type) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            Path directory = Paths.get(uploadDir, type);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            Path filePath = directory.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            File savedFile = fileRepository.save(File.builder()
                    .originalName(originalFilename)
                    .fileName(filename)
                    .filePath(filePath.toString())
                    .fileType(type)
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .build());
            
            return convertToDTO(savedFile);
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileDTO getFile(Long id) {
        return fileRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        
        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
            fileRepository.delete(file);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FileDTO> getFiles(String type, Pageable pageable) {
        return fileRepository.findByFileType(type, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileDTO> getFilesByType(String type) {
        return fileRepository.findByFileType(type).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public FileDTO updateFile(Long id, MultipartFile newFile) {
        File existingFile = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        
        try {
            // 기존 파일 삭제
            Files.deleteIfExists(Paths.get(existingFile.getFilePath()));
            
            // 새 파일 저장
            String originalFilename = newFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            Path directory = Paths.get(uploadDir, existingFile.getFileType());
            Path filePath = directory.resolve(filename);
            Files.copy(newFile.getInputStream(), filePath);
            
            // 파일 정보 업데이트
            existingFile.update(
                originalFilename,
                filename,
                filePath.toString(),
                newFile.getSize(),
                newFile.getContentType()
            );
            
            return convertToDTO(fileRepository.save(existingFile));
        } catch (IOException e) {
            log.error("파일 업데이트 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업데이트에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public void deleteFilesByType(String type) {
        List<File> files = fileRepository.findByFileType(type);
        for (File file : files) {
            try {
                Files.deleteIfExists(Paths.get(file.getFilePath()));
                fileRepository.delete(file);
            } catch (IOException e) {
                log.error("파일 삭제 실패: {}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileDTO getFileByUrl(String url) {
        return fileRepository.findByFilePath(url)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public void deleteFileByUrl(String url) {
        File file = fileRepository.findByFilePath(url)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        
        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
            fileRepository.delete(file);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional
    public List<FileDTO> uploadFiles(List<MultipartFile> files, String type) {
        return files.stream()
                .map(file -> uploadFile(file, type))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileUrl(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        return file.getFilePath();
    }

    private FileDTO convertToDTO(File file) {
        return new FileDTO(
            file.getId(),
            file.getOriginalName(),
            file.getFileName(),
            file.getFilePath(),
            file.getFileType(),
            file.getFileSize(),
            file.getMimeType(),
            file.getCreatedAt(),
            file.getUpdatedAt()
        );
    }
}