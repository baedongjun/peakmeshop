package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.FileDTO;
import com.peakmeshop.domain.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String type) {
        return ResponseEntity.ok(fileService.uploadFile(file, type));
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<List<FileDTO>> uploadFiles(@RequestParam("files") List<MultipartFile> files, @PathVariable String type) {
        return ResponseEntity.ok(fileService.uploadFiles(files, type));
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{filename}")
    public ResponseEntity<FileDTO> getFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFile(id));
    }

    @GetMapping("/{filename}/url")
    public ResponseEntity<String> getFileUrl(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileUrl(id));
    }
} 