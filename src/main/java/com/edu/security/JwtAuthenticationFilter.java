package com.edu.security;

import com.edu.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 跳过不需要认证的路径
        if (isSkipAuth(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头获取token
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                // 验证token
                if (jwtUtils.validateToken(token)) {
                    // 设置用户信息到请求属性中
                    String username = jwtUtils.getUsernameFromToken(token);
                    Long userId = jwtUtils.getUserIdFromToken(token);
                    Integer role = jwtUtils.getRoleFromToken(token);

                    request.setAttribute("username", username);
                    request.setAttribute("userId", userId);
                    request.setAttribute("role", role);

                    log.debug("用户认证成功: username={}, userId={}, role={}", username, userId, role);
                } else {
                    log.warn("无效的token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("无效的token");
                    return;
                }
            } catch (Exception e) {
                log.error("Token验证失败", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token验证失败");
                return;
            }
        } else {
            log.warn("请求缺少认证token: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("缺少认证token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtUtils.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtUtils.getTokenHead() + " ")) {
            return bearerToken.substring(jwtUtils.getTokenHead().length() + 1);
        }
        return null;
    }

    /**
     * 判断是否跳过认证
     */
    private boolean isSkipAuth(String requestURI) {
        // 登录、注册接口跳过认证
        if (requestURI.startsWith("/api/auth/login") ||
            requestURI.startsWith("/api/auth/register") ||
            requestURI.startsWith("/swagger") ||
            requestURI.startsWith("/v2/api-docs") ||
            requestURI.startsWith("/webjars") ||
            requestURI.startsWith("/swagger-resources")) {
            return true;
        }
        return false;
    }
}