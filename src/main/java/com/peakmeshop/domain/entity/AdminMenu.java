package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin_menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMenu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;        // 메뉴 이름

    @Column(length = 255)
    private String url;         // 메뉴 URL

    @Column(length = 100)
    private String icon;        // 메뉴 아이콘 (Font Awesome 클래스명)

    private Integer sortOrder;  // 정렬 순서

    private Boolean isVisible;  // 표시 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AdminMenu parent;   // 상위 메뉴

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("sortOrder ASC")
    private List<AdminMenu> children = new ArrayList<>();  // 하위 메뉴 목록

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 하위 메뉴 추가 메서드
    public void addChild(AdminMenu child) {
        children.add(child);
        child.setParent(this);
    }

    // 하위 메뉴 제거 메서드
    public void removeChild(AdminMenu child) {
        children.remove(child);
        child.setParent(null);
    }
} 