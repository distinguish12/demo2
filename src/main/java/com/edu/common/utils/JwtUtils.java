package com.edu.common.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret:edu-online-secret-key-2024}")
    private String secret;

    @Value("${jwt.expire:604800000}")
    private Long expire;

    @Value("${jwt.header:Authorization}")
    private String header;

    @Value("${jwt.token-head:Bearer}")
    private String tokenHead;

    /**
     * 生成token
     */
    public String generateToken(String username, Long userId, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("username", username);

        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 解析token
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的Token: {}", e.getMessage());
            throw new RuntimeException("不支持的Token");
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (SignatureException e) {
            log.warn("Token签名错误: {}", e.getMessage());
            throw new RuntimeException("Token签名错误");
        } catch (IllegalArgumentException e) {
            log.warn("Token参数错误: {}", e.getMessage());
            throw new RuntimeException("Token参数错误");
        }
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从token中获取用户角色
     */
    public Integer getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Integer.valueOf(claims.get("role").toString());
    }

    /**
     * 验证token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证token
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject();
        Long userId = Long.valueOf(claims.get("userId").toString());
        Integer role = Integer.valueOf(claims.get("role").toString());
        return generateToken(username, userId, role);
    }

    // Getter methods
    public String getSecret() {
        return secret;
    }

    public Long getExpire() {
        return expire;
    }

    public String getHeader() {
        return header;
    }

    public String getTokenHead() {
        return tokenHead;
    }
}