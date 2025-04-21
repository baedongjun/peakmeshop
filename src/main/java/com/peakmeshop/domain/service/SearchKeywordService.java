package com.peakmeshop.domain.service;

import com.peakmeshop.domain.entity.SearchKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchKeywordService {
    
    void recordSearch(String keyword);
    
    Page<SearchKeyword> getPopularKeywords(Pageable pageable);
    
    Page<SearchKeyword> getSimilarKeywords(String keyword, Pageable pageable);
    
    List<SearchKeyword> getSuggestKeywords(String prefix, int limit);
    
    Page<SearchKeyword> searchKeywords(String keyword, Pageable pageable);
} 