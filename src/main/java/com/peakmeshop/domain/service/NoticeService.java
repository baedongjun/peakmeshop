package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.NoticeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NoticeService {
    
    /**
     * 공지사항 생성
     */
    NoticeDTO createNotice(NoticeDTO noticeDTO);
    
    /**
     * 공지사항 수정
     */
    NoticeDTO updateNotice(Long id, NoticeDTO noticeDTO);
    
    /**
     * 공지사항 삭제
     */
    void deleteNotice(Long id);
    
    /**
     * 공지사항 상세 조회
     */
    NoticeDTO getNoticeById(Long id);
    
    /**
     * 공지사항 목록 조회 (페이징)
     */
    Page<NoticeDTO> getAllNotices(Pageable pageable);
    
    /**
     * 공지사항 목록 조회 (전체)
     */
    List<NoticeDTO> getAllNotices();
    
    /**
     * 중요 공지사항 목록 조회
     */
    List<NoticeDTO> getImportantNotices();
    
    /**
     * 카테고리별 공지사항 목록 조회
     */
    Page<NoticeDTO> getNoticesByCategory(String category, Pageable pageable);
    
    /**
     * 키워드로 공지사항 검색
     */
    Page<NoticeDTO> searchNotices(String keyword, Pageable pageable);
    
    /**
     * 공지사항 조회수 증가
     */
    void incrementViewCount(Long id);
    
    /**
     * 공지사항 상태 변경 (게시/숨김)
     */
    NoticeDTO updateNoticeStatus(Long id, String status);
    
    /**
     * 중요 공지사항 설정/해제
     */
    NoticeDTO toggleImportant(Long id);
    
    /**
     * 기간별 공지사항 조회
     */
    Page<NoticeDTO> getNoticesByPeriod(String startDate, String endDate, Pageable pageable);
    
    /**
     * 최근 공지사항 조회
     */
    List<NoticeDTO> getRecentNotices(int limit);
    
    /**
     * 메인 페이지용 공지사항 목록 조회
     */
    List<NoticeDTO> getMainPageNotices();
    
    /**
     * 공지사항 통계 조회
     */
    Map<String, Object> getNoticeStatistics(String period);
} 