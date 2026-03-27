package com.financas.controle_gastos.service;

import com.financas.controle_gastos.model.Usuario;
import com.financas.controle_gastos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String loginDigitado) throws UsernameNotFoundException {
        // 1. Busca o usuário no banco usando o campo 'username'
        Usuario usuario = repository.findByUsername(loginDigitado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + loginDigitado));

        // 2. Converte para o UserDetails do Spring
        return User.builder()
                .username(usuario.getUsername()) // Certifique-se que o getter na classe Usuario é getUsername()
                .password(usuario.getSenha())    // Ou getPassword(), dependendo da sua classe Usuario
                .roles(usuario.getRole().name())
                .build();
    }
}