package com.peakmeshop.api.dto;

import java.time.LocalDateTime;

public record FaqDTO(
    Long id,
    String question,
    String answer,
    String category,
    Integer sortOrder,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static FaqDTOBuilder builder() {
        return new FaqDTOBuilder();
    }

    public static class FaqDTOBuilder {
        private Long id;
        private String question;
        private String answer;
        private String category;
        private Integer sortOrder;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        FaqDTOBuilder() {
        }

        public FaqDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FaqDTOBuilder question(String question) {
            this.question = question;
            return this;
        }

        public FaqDTOBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public FaqDTOBuilder category(String category) {
            this.category = category;
            return this;
        }

        public FaqDTOBuilder sortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public FaqDTOBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public FaqDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FaqDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public FaqDTO build() {
            return new FaqDTO(id, question, answer, category, sortOrder, isActive, createdAt, updatedAt);
        }
    }
} 