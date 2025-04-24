package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.TagDTO;

public interface TagService {
    Page<TagDTO> getTags(Pageable pageable);
    List<TagDTO> getAllTags();
    Optional<TagDTO> getTagById(Long id);
    Optional<TagDTO> getTagBySlug(String slug);
    TagDTO createTag(TagDTO tagDTO);
    TagDTO updateTag(Long id, TagDTO tagDTO);
    void deleteTag(Long id);
    List<TagDTO> getPopularTags(int limit);
    List<TagDTO> searchTags(String keyword);
    List<TagDTO> getTagsWithProducts();
} 