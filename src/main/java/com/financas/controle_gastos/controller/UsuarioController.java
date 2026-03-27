package com.financas.controle_gastos.controller;

import com.financas.controle_gastos.model.Usuario;
import com.financas.controle_gastos.service.JwtService;
import com.financas.controle_gastos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // Rota: http://localhost:8080/auth/register
    @PostMapping("/register")
    public ResponseEntity<Usuario> salvar(@RequestBody Usuario usuario) {
        // Certifique-se que o service.salvar usa o passwordEncoder.encode()
        return ResponseEntity.ok(service.salvar(usuario));
    }

    // Rota: http://localhost:8080/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        System.out.println("--- TENTATIVA DE LOGIN ---");
        System.out.println("Login enviado: " + usuario.getUsername());
        System.out.println("--------------------------");

        // 1. Autentica no Spring Security
        // Se a senha estiver errada, o Spring lança BadCredentialsException aqui.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuario.getSenha())
        );

        // 2. Busca o usuário completo do banco (agora com ID, Role ADMIN, etc.)
        // Isso evita que o token seja gerado com informações nulas.
        Usuario usuarioLogado = service.buscarPorLogin(usuario.getUsername())
                .orElseThrow(() -> new RuntimeException("Erro interno: Usuário autenticado mas não encontrado no banco."));

        // 3. Gera o token
        // Se o seu JwtService.gerarToken aceitar String, passe o login.
        // Se aceitar o objeto Usuario, passe o 'usuarioLogado'.
        String token = jwtService.gerarToken(usuarioLogado.getUsername());

        // 4. Retorna o JSON com o token para o Angular
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // Rota: http://localhost:8080/auth
    @GetMapping
    public List<Usuario> listar() {
        return service.listarTodos();
    }
}