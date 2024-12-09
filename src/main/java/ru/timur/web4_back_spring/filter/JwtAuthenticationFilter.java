package ru.timur.web4_back_spring.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.timur.web4_back_spring.service.AccessTokenService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final RequestMatcher ignore = new OrRequestMatcher(
            new AntPathRequestMatcher("/auth/**", HttpMethod.GET.name()),
            new AntPathRequestMatcher("/auth/**", HttpMethod.POST.name())
    );

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer";

    private final AccessTokenService accessTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURL = request.getRequestURL().toString();
        log.info("Received request with url {}", requestURL);

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        log.info("Received authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Bearer token was not found");
            response.sendError(HttpStatus.FORBIDDEN.value(), "Bearer token was not found");
            return;
        }

        final String token;
        token = authHeader.substring(BEARER_PREFIX.length()).trim();
        log.info("Received bearer token: {}", token);

        try {
            accessTokenService.verifyToken(token);
        } catch (JWTVerificationException e) {
            log.warn("Token verification failed: {}", e.getMessage());
            response.sendError(HttpStatus.FORBIDDEN.value(), "Token verification failed");
            return;
        }

        final String username;
        username = accessTokenService.extractUsername(token);
        log.info("Received username {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return ignore.matches(request);
    }
}
