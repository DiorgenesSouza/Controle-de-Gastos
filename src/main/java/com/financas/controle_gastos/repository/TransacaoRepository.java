package com.financas.controle_gastos.repository;

import com.financas.controle_gastos.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    // Este método permite buscar apenas as transações vinculadas a um usuário específico
    List<Transacao> findByUsuarioId(Long usuarioId);

}