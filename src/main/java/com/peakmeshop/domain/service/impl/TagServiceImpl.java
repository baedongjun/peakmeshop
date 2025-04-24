package com.peakmeshop.domain.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.TagDTO;
import com.peakmeshop.api.mapper.TagMapper;
import com.peakmeshop.domain.entity.Tag;
import com.peakmeshop.domain.repository.TagRepository;
import com.peakmeshop.domain.service.TagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Page<TagDTO> getTags(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tagMapper::toDTO);
    }

    @Override
    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<TagDTO> getTagById(Long id) {
        return tagRepository.findById(id)
                .map(tagMapper::toDTO);
    }

    @Override
    public Optional<TagDTO> getTagBySlug(String slug) {
        return tagRepository.findBySlug(slug)
                .map(tagMapper::toDTO);
    }

    @Override
    @Transactional
    public TagDTO createTag(TagDTO tagDTO) {
        Tag tag = tagMapper.toEntity(tagDTO);
        tag = tagRepository.save(tag);
        return tagMapper.toDTO(tag);
    }

    @Override
    @Transactional
    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        
        tag.setName(tagDTO.getName());
        tag.setSlug(tagDTO.getSlug());
        tag.setDescription(tagDTO.getDescription());
        
        tag = tagRepository.save(tag);
        return tagMapper.toDTO(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<TagDTO> getPopularTags(int limit) {
        return tagRepository.findPopularTags(limit).stream()
                .map(tagMapper::toDTO)
                .toList();
    }

    @Override
    public List<TagDTO> searchTags(String keyword) {
        return tagRepository.searchTags(keyword).stream()
                .map(tagMapper::toDTO)
                .toList();
    }

    @Override
    public List<TagDTO> getTagsWithProducts() {
        return tagRepository.findTagsWithProducts().stream()
                .map(tagMapper::toDTO)
                .toList();
    }
} 