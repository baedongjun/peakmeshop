package com.peakmeshop.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peakmeshop.dto.FileDTO;
import com.peakmeshop.service.FileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "uploads") String directory) {

        FileDTO uploadedFile = fileService.uploadFile(file, directory);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
    }

    @PostMapping("/uploads")
    public ResponseEntity<List<FileDTO>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "directory", defaultValue = "uploads") String directory) {

        List<FileDTO> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFiles.add(fileService.uploadFile(file, directory));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFiles);
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            byte[] fileContent = fileService.getFileContent(fileName);

            // 파일 타입 결정
            String contentType = request.getServletContext().getMimeType(fileName);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(new org.springframework.core.io.ByteArrayResource(fileContent));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/view/{fileName:.+}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            byte[] fileContent = fileService.getFileContent(fileName);

            // 파일 타입 결정
            String contentType = request.getServletContext().getMimeType(fileName);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        try {
            fileService.deleteFile(fileName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}