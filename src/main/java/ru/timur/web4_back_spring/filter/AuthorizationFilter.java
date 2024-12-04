package ru.timur.web4_back_spring.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.timur.web4_back_spring.exception.SessionNotFoundException;
import ru.timur.web4_back_spring.exception.SessionTimeoutException;
import ru.timur.web4_back_spring.service.UserSessionService;
import ru.timur.web4_back_spring.util.JWTUtil;
import ru.timur.web4_back_spring.util.UserPrincipals;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserSessionService userSessionService;

    @Autowired
    public AuthorizationFilter(UserSessionService userSessionService, JWTUtil jwtUtil) {
        this.userSessionService = userSessionService;
        this.jwtUtil = jwtUtil;
    }

    private static final Set<String> SKIP_PATHS = new HashSet<>(Arrays.asList(
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh-token"
    ));

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);  // Пропускаем OPTIONS запросы
            return;
        }

        String path = request.getRequestURI();
        log.info("Received request with path: {}", path);
        if (SKIP_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        log.info("Received request with header: {}", authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Authorization header not found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header must be provided");
            return;
        }

        String token = authHeader.substring("Bearer".length()).trim();
        log.info("Received token: {}", token);

        try {
            userSessionService.validateToken(token);
        } catch (SessionNotFoundException e) {
            log.info("Session not found for token: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        } catch (SessionTimeoutException e) {
            log.info("Session timeout for token: {}", token);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Login timeout expired");
            return;
        }

        //TODO: remove jwtUtil from this module
        Long userId = jwtUtil.getUserId(token);
        UserPrincipals userPrincipals = new UserPrincipals(userId, token);
        SecurityContextHolder.getContext().setAuthentication(userPrincipals);

        filterChain.doFilter(request, response);
    }
}
