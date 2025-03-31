package com.peakmeshop.domain.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.QnaDTO;

public interface QnaService {

    Page<QnaDTO> getAllQnas(Pageable pageable);

    Page<QnaDTO> getQnasByProductId(Long productId, Pageable pageable);

    Page<QnaDTO> getQnasByMemberId(Long memberId, Pageable pageable);

    Optional<QnaDTO> getQnaById(Long id);

    QnaDTO createQna(QnaDTO qnaDTO);

    QnaDTO updateQna(Long id, QnaDTO qnaDTO);

    QnaDTO answerQna(Long id, String answer, Long adminId);

    boolean deleteQna(Long id, Long memberId);

    Page<QnaDTO> getQnasByStatus(String status, Pageable pageable);
}