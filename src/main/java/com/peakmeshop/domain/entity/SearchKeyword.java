package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_keywords")
@Getter
@NoArgsConstructor
public class SearchKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private int searchCount;

    public SearchKeyword(String keyword) {
        this.keyword = keyword;
        this.searchCount = 1;
    }

    public void incrementSearchCount() {
        this.searchCount++;
    }
} 