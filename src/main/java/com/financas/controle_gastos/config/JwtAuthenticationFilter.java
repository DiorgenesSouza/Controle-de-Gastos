package com.financas.controle_gastos.config;

import com.financas.controle_gastos.service.JwtService;
import com.financas.controle_gastos.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 1. Verifica se o Header existe e começa com Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            // LIMPEZA EXTRA: Remove aspas que o LocalStorage do Angular costuma adicionar
//            if (token != null) {
//                token = token.trim().replace("\"", "");
//            }

            // No seu JwtAuthenticationFilter.java
            if (token != null) {
                token = token.trim();

                // Se o token começar com {"token": , vamos extrair só o valor
                if (token.contains("{\"token\":\"")) {
                    token = token.substring(token.indexOf("\":\"") + 3, token.lastIndexOf("\"}"));
                }

                // Remove aspas simples que sobrarem
                token = token.replace("\"", "");
            }

            try {
                // 2. Tenta extrair o username do token
                username = jwtService.validarToken(token);
            } catch (Exception e) {
                // Se o token for inválido ou estiver mal formatado, logamos mas não travamos a requisição
                System.out.println("Erro ao validar Token JWT: " + e.getMessage());
            }
        }

        // 3. Se encontrou o usuário e ele ainda não está autenticado na sessão
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Define a autenticação no contexto do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua o fluxo para o próximo filtro ou para o Controller
        filterChain.doFilter(request, response);
    }
}