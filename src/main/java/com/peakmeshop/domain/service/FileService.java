package com.peakmeshop.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.api.dto.FileDTO;

public interface FileService {
    FileDTO uploadFile(MultipartFile file, String type);
    List<FileDTO> uploadFiles(List<MultipartFile> files, String type);
    FileDTO getFile(Long id);
    void deleteFile(Long id);
    Page<FileDTO> getFiles(String type, Pageable pageable);
    List<FileDTO> getFilesByType(String type);
    FileDTO updateFile(Long id, MultipartFile file);
    void deleteFilesByType(String type);
    FileDTO getFileByUrl(String url);
    void deleteFileByUrl(String url);
    String getFileUrl(Long id);
}