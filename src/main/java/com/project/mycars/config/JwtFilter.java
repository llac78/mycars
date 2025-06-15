package com.project.mycars.config;

import com.project.mycars.model.User;
import com.project.mycars.repository.UserRepository;
import com.project.mycars.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;
    private final UserRepository userRepository;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, MessageSource messageSource, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.messageSource = messageSource;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 Permite que requisições passem sem validação JWT
        if (path.startsWith("/api/users") || path.equals("/api/signin") || path.equals("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            String message = messageSource.getMessage("user.token.unauthorized", null, Locale.getDefault());
            sendUnauthorizedResponse(response, message);
            return;
        }

        String token = header.substring(7);
        if (token.isBlank()) {
            String message = messageSource.getMessage("user.token.unauthorized", null, Locale.getDefault());
            sendUnauthorizedResponse(response, message);
            return;
        }
        try {
            if (!jwtUtil.validateToken(token)) {
                String message = messageSource.getMessage("user.token.unauthorized.invalid.session", null, Locale.getDefault());
                sendUnauthorizedResponse(response, message);
                return;
            }
            String username = jwtUtil.extractUsername(token);
            Optional<User> userBD = userRepository.findByLogin(username);
            if (userBD.isEmpty()) {
                String message = messageSource.getMessage("user.not.found", null, Locale.getDefault());
                sendUnauthorizedResponse(response, message);
                return;
            }
            User user = userBD.get();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("Usuário autenticado via JWT: " + user.getLogin());

        } catch (ExpiredJwtException e) {
            sendUnauthorizedResponse(response, "Unauthorized - invalid session");
            return;
        }
        chain.doFilter(request, response);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\", \"errorCode\": 401}");
    }
}

