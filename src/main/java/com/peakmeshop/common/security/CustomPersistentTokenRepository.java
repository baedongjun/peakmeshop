package com.peakmeshop.common.security;

import jakarta.activation.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPersistentTokenRepository implements PersistentTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    // 테이블 이름과 컬럼 이름 설정
    private static final String TABLE_NAME = "persistent_logins";
    private static final String USERNAME_COLUMN = "username";
    private static final String SERIES_COLUMN = "series";
    private static final String TOKEN_COLUMN = "token";
    private static final String LAST_USED_COLUMN = "last_used";

    // SQL 쿼리 정의
    private static final String CREATE_TOKEN_SQL = "INSERT INTO " + TABLE_NAME + " (" +
            USERNAME_COLUMN + ", " + SERIES_COLUMN + ", " + TOKEN_COLUMN + ", " + LAST_USED_COLUMN +
            ") VALUES (?, ?, ?, ?)";

    private static final String UPDATE_TOKEN_SQL = "UPDATE " + TABLE_NAME + " SET " +
            TOKEN_COLUMN + " = ?, " + LAST_USED_COLUMN + " = ? WHERE " + SERIES_COLUMN + " = ?";

    private static final String REMOVE_USER_TOKENS_SQL = "DELETE FROM " + TABLE_NAME +
            " WHERE " + USERNAME_COLUMN + " = ?";

    private static final String REMOVE_TOKEN_SQL = "DELETE FROM " + TABLE_NAME +
            " WHERE " + SERIES_COLUMN + " = ?";

    private static final String LOAD_TOKEN_SQL = "SELECT " + USERNAME_COLUMN + ", " +
            SERIES_COLUMN + ", " + TOKEN_COLUMN + ", " + LAST_USED_COLUMN +
            " FROM " + TABLE_NAME + " WHERE " + SERIES_COLUMN + " = ?";

    @Override
    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        log.info("새로운 Remember Me 토큰 생성: {}", token.getUsername());
        try {
            jdbcTemplate.update(CREATE_TOKEN_SQL,
                    token.getUsername(),
                    token.getSeries(),
                    token.getTokenValue(),
                    token.getDate());
        } catch (DataAccessException e) {
            log.error("토큰 생성 중 오류 발생", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        log.info("Remember Me 토큰 업데이트: {}", series);
        try {
            jdbcTemplate.update(UPDATE_TOKEN_SQL, tokenValue, lastUsed, series);
        } catch (DataAccessException e) {
            log.error("토큰 업데이트 중 오류 발생", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PersistentRememberMeToken getTokenForSeries(String series) {
        log.info("시리즈로 Remember Me 토큰 조회: {}", series);
        try {
            return jdbcTemplate.queryForObject(LOAD_TOKEN_SQL, (rs, rowNum) -> {
                return new PersistentRememberMeToken(
                        rs.getString(USERNAME_COLUMN),
                        rs.getString(SERIES_COLUMN),
                        rs.getString(TOKEN_COLUMN),
                        rs.getTimestamp(LAST_USED_COLUMN)
                );
            }, series);
        } catch (DataAccessException e) {
            log.error("토큰 조회 중 오류 발생", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void removeUserTokens(String username) {
        log.info("사용자의 모든 Remember Me 토큰 삭제: {}", username);
        try {
            jdbcTemplate.update(REMOVE_USER_TOKENS_SQL, username);
        } catch (DataAccessException e) {
            log.error("사용자 토큰 삭제 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 특정 시리즈의 토큰을 삭제합니다.
     */
    @Transactional
    public void removeToken(String series) {
        log.info("특정 Remember Me 토큰 삭제: {}", series);
        try {
            jdbcTemplate.update(REMOVE_TOKEN_SQL, series);
        } catch (DataAccessException e) {
            log.error("토큰 삭제 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 만료된 토큰을 정리합니다.
     */
    @Transactional
    public void cleanUpExpiredTokens(Date expiryAt) {
        log.info("만료된 Remember Me 토큰 정리: {}", expiryAt);
        try {
            String cleanUpSql = "DELETE FROM " + TABLE_NAME + " WHERE " + LAST_USED_COLUMN + " < ?";
            int count = jdbcTemplate.update(cleanUpSql, expiryAt);
            log.info("{}개의 만료된 토큰이 삭제되었습니다.", count);
        } catch (DataAccessException e) {
            log.error("만료된 토큰 정리 중 오류 발생", e);
            throw e;
        }
    }
}