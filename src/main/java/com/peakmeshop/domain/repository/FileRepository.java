package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByFilePath(String filePath);
    Page<File> findByFileType(String fileType, Pageable pageable);
    List<File> findByFileType(String fileType);

    File findByFileName(String filename);
}