package com.financas.controle_gastos.service;

import com.financas.controle_gastos.enums.Role;
import com.financas.controle_gastos.model.Usuario;
import com.financas.controle_gastos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // NOVO MÉTODO: Essencial para o Controller funcionar agora
    public Optional<Usuario> buscarPorLogin(String login) {
        // Mude de findByLogin para findByUsername
        return repository.findByUsername(login);
    }

    public Usuario salvar(Usuario usuario) {
        // 1. Criptografamos a senha antes de persistir no banco
        // Ajustado: pegando do getSenha() que é o que vem do seu Model/Angular
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        // 2. Garante que o usuário tenha uma Role padrão (USER) se vier nulo
        if (usuario.getRole() == null) {
            usuario.setRole(Role.USER);
        }

        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    // Método utilitário que você já tinha
    public void resetarSenhaDiorgenes() {
        // Usamos findByUsername e o .orElseThrow para pegar o usuário ou dar erro
        Usuario usuario = repository.findByUsername("diorgenes")
                .orElseThrow(() -> new RuntimeException("Usuário diorgenes não encontrado no banco"));

        String senhaCriptografada = passwordEncoder.encode("123");
        usuario.setSenha(senhaCriptografada);

        repository.save(usuario);

        System.out.println("--------------------------------------------------");
        System.out.println("SUCESSO: SENHA RESETADA PELO JAVA (usando USERNAME)");
        System.out.println("--------------------------------------------------");
    }
}