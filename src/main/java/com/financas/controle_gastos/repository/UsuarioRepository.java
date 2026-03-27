package com.financas.controle_gastos.repository;

import com.financas.controle_gastos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring vai procurar o campo 'login' na classe Usuario.java
    Optional<Usuario> findByUsername(String username);
}