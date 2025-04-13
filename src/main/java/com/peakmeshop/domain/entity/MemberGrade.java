package com.peakmeshop.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member_grades")
@Getter
@Setter
public class MemberGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "condition_type", nullable = false)
    private String conditionType;

    @Column(name = "condition_value", nullable = false)
    private int conditionValue;

    @Column(name = "benefit_type", nullable = false)
    private String benefitType;

    @Column(name = "benefit_value", nullable = false)
    private int benefitValue;

    @Column(name = "is_free_shipping", nullable = false)
    private boolean isFreeShipping = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
} 