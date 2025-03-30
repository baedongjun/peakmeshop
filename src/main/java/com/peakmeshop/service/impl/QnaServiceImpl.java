package com.peakmeshop.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.dto.QnaDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.Product;
import com.peakmeshop.entity.Qna;
import com.peakmeshop.exception.ResourceNotFoundException;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.ProductRepository;
import com.peakmeshop.repository.QnaRepository;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.QnaService;

@Service
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public QnaServiceImpl(
            QnaRepository qnaRepository,
            MemberRepository memberRepository,
            ProductRepository productRepository,
            EmailService emailService) {
        this.qnaRepository = qnaRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QnaDTO> getAllQnas(Pageable pageable) {
        return qnaRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QnaDTO> getQnasByProductId(Long productId, Pageable pageable) {
        return qnaRepository.findByProductId(productId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QnaDTO> getQnasByMemberId(Long memberId, Pageable pageable) {
        return qnaRepository.findByMemberId(memberId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QnaDTO> getQnaById(Long id) {
        return qnaRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public QnaDTO createQna(QnaDTO qnaDTO) {
        // 회원 조회
        Member member = memberRepository.findById(qnaDTO.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다. ID: " + qnaDTO.memberId()));

        // 상품 조회
        Product product = null;
        if (qnaDTO.productId() != null) {
            product = productRepository.findById(qnaDTO.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다. ID: " + qnaDTO.productId()));
        }

        // Q&A 생성
        Qna qna = new Qna();
        qna.setMember(member);
        qna.setProduct(product);
        qna.setTitle(qnaDTO.title());
        qna.setContent(qnaDTO.content());
        qna.setSecret(qnaDTO.isSecret());
        qna.setStatus("PENDING");
        qna.setCreatedAt(LocalDateTime.now());
        qna.setUpdatedAt(LocalDateTime.now());

        Qna savedQna = qnaRepository.save(qna);

        // 관리자에게 새 Q&A 알림 이메일 발송
        // 실제 구현에서는 관리자 이메일 주소를 설정 파일에서 가져오거나 관리자 계정에서 조회
        // emailService.sendNewQnaNotificationEmail("admin@peakmeshop.com", convertToDTO(savedQna));

        return convertToDTO(savedQna);
    }

    @Override
    @Transactional
    public QnaDTO updateQna(Long id, QnaDTO qnaDTO) {
        Qna existingQna = qnaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Q&A를 찾을 수 없습니다. ID: " + id));

        // 작성자 확인
        if (!existingQna.getMember().getId().equals(qnaDTO.memberId())) {
            throw new IllegalStateException("Q&A 작성자만 수정할 수 있습니다.");
        }

        // 답변이 달린 Q&A는 수정 불가
        if (existingQna.getAnswer() != null && !existingQna.getAnswer().isEmpty()) {
            throw new IllegalStateException("답변이 달린 Q&A는 수정할 수 없습니다.");
        }

        existingQna.setTitle(qnaDTO.title());
        existingQna.setContent(qnaDTO.content());
        existingQna.setSecret(qnaDTO.isSecret());
        existingQna.setUpdatedAt(LocalDateTime.now());

        Qna updatedQna = qnaRepository.save(existingQna);
        return convertToDTO(updatedQna);
    }

    @Override
    @Transactional
    public QnaDTO answerQna(Long id, String answer, Long adminId) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Q&A를 찾을 수 없습니다. ID: " + id));

        // 관리자 확인
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("관리자를 찾을 수 없습니다. ID: " + adminId));

        if (!"ROLE_ADMIN".equals(admin.getUserRole())) {
            throw new IllegalStateException("관리자만 답변을 작성할 수 있습니다.");
        }

        qna.setAnswer(answer);
        qna.setAnsweredBy(admin);
        qna.setAnsweredAt(LocalDateTime.now());
        qna.setStatus("ANSWERED");
        qna.setUpdatedAt(LocalDateTime.now());

        Qna answeredQna = qnaRepository.save(qna);

        // 질문 작성자에게 답변 알림 이메일 발송
        if (qna.getMember() != null && qna.getMember().getEmail() != null) {
            // emailService.sendQnaAnswerNotificationEmail(qna.getMember().getEmail(), convertToDTO(answeredQna));
        }

        return convertToDTO(answeredQna);
    }

    @Override
    @Transactional
    public boolean deleteQna(Long id, Long memberId) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Q&A를 찾을 수 없습니다. ID: " + id));

        // 작성자 또는 관리자 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다. ID: " + memberId));

        if (!qna.getMember().getId().equals(memberId) && !"ROLE_ADMIN".equals(member.getUserRole())) {
            throw new IllegalStateException("Q&A 작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        qnaRepository.delete(qna);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QnaDTO> getQnasByStatus(String status, Pageable pageable) {
        return qnaRepository.findByStatus(status, pageable)
                .map(this::convertToDTO);
    }

    // 엔티티를 DTO로 변환
    private QnaDTO convertToDTO(Qna qna) {
        return new QnaDTO(
                qna.getId(),
                qna.getMember().getId(),
                qna.getMember().getName(),
                qna.getProduct() != null ? qna.getProduct().getId() : null,
                qna.getProduct() != null ? qna.getProduct().getName() : null,
                qna.getTitle(),
                qna.getContent(),
                qna.isSecret(),
                qna.getStatus(),
                qna.getAnswer(),
                qna.getAnsweredBy() != null ? qna.getAnsweredBy().getId() : null,
                qna.getAnsweredBy() != null ? qna.getAnsweredBy().getName() : null,
                qna.getAnsweredAt(),
                qna.getCreatedAt(),
                qna.getUpdatedAt()
        );
    }
}