package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.NoticeDTO;
import com.peakmeshop.domain.entity.Notice;
import com.peakmeshop.domain.repository.NoticeRepository;
import com.peakmeshop.domain.service.NoticeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional
    public NoticeDTO createNotice(NoticeDTO noticeDTO) {
        Notice notice = Notice.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .category(noticeDTO.getCategory())
                .important(noticeDTO.getImportant())
                .status("ACTIVE")
                .viewCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notice savedNotice = noticeRepository.save(notice);
        return convertToDTO(savedNotice);
    }

    @Override
    @Transactional
    public NoticeDTO updateNotice(Long id, NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));

        notice.update(
            noticeDTO.getTitle(),
            noticeDTO.getContent(),
            noticeDTO.getCategory(),
            noticeDTO.getImportant()
        );

        Notice updatedNotice = noticeRepository.save(notice);
        return convertToDTO(updatedNotice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        noticeRepository.delete(notice);
    }

    @Override
    public NoticeDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        return convertToDTO(notice);
    }

    @Override
    public Page<NoticeDTO> getAllNotices(Pageable pageable) {
        Page<Notice> noticePage = noticeRepository.findAllByOrderByImportantDescCreatedAtDesc(pageable);
        return noticePage.map(this::convertToDTO);
    }

    @Override
    public List<NoticeDTO> getAllNotices() {
        List<Notice> notices = noticeRepository.findAllByOrderByImportantDescCreatedAtDesc();
        return notices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoticeDTO> getImportantNotices() {
        List<Notice> notices = noticeRepository.findByImportantTrueAndStatusOrderByCreatedAtDesc("ACTIVE");
        return notices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NoticeDTO> getNoticesByCategory(String category, Pageable pageable) {
        Page<Notice> noticePage = noticeRepository.findByCategoryAndStatusOrderByImportantDescCreatedAtDesc(
            category, "ACTIVE", pageable);
        return noticePage.map(this::convertToDTO);
    }

    @Override
    public Page<NoticeDTO> searchNotices(String keyword, Pageable pageable) {
        Page<Notice> noticePage = noticeRepository.findByTitleContainingOrContentContainingOrderByCreatedAtDesc(
            keyword, keyword, pageable);
        return noticePage.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        notice.incrementViewCount();
        noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public NoticeDTO updateNoticeStatus(Long id, String status) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        notice.setStatus(status);
        Notice updatedNotice = noticeRepository.save(notice);
        return convertToDTO(updatedNotice);
    }

    @Override
    @Transactional
    public NoticeDTO toggleImportant(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다."));
        notice.setImportant(!notice.getImportant());
        Notice updatedNotice = noticeRepository.save(notice);
        return convertToDTO(updatedNotice);
    }

    @Override
    public Page<NoticeDTO> getNoticesByPeriod(String startDate, String endDate, Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, formatter).plusDays(1).atStartOfDay();

        Page<Notice> noticePage = noticeRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(
            start, end, pageable);
        return noticePage.map(this::convertToDTO);
    }

    @Override
    public List<NoticeDTO> getRecentNotices(int limit) {
        List<Notice> notices = noticeRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", Pageable.ofSize(limit));
        return notices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoticeDTO> getMainPageNotices() {
        List<Notice> importantNotices = noticeRepository.findByImportantTrueAndStatusOrderByCreatedAtDesc("ACTIVE");
        List<Notice> recentNotices = noticeRepository.findByStatusOrderByCreatedAtDesc("ACTIVE", Pageable.ofSize(5));

        return Stream.concat(importantNotices.stream(), recentNotices.stream())
                .distinct()
                .limit(5)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getNoticeStatistics(String period) {
        Map<String, Object> statistics = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (period) {
            case "daily":
                start = now.minusDays(1);
                break;
            case "weekly":
                start = now.minusWeeks(1);
                break;
            case "monthly":
                start = now.minusMonths(1);
                break;
            case "yearly":
                start = now.minusYears(1);
                break;
            default:
                start = now.minusMonths(1);
        }

        long totalCount = noticeRepository.countByCreatedAtBetween(start, now);
        long importantCount = noticeRepository.countByImportantTrueAndCreatedAtBetween(start, now);
        long activeCount = noticeRepository.countByStatusAndCreatedAtBetween("ACTIVE", start, now);
        double avgViewCount = noticeRepository.getAverageViewCountByPeriod(start, now);

        statistics.put("totalCount", totalCount);
        statistics.put("importantCount", importantCount);
        statistics.put("activeCount", activeCount);
        statistics.put("avgViewCount", avgViewCount);

        return statistics;
    }

    private NoticeDTO convertToDTO(Notice notice) {
        return NoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .category(notice.getCategory())
                .important(notice.getImportant())
                .status(notice.getStatus())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
} 