package com.financas.controle_gastos.service;

import com.financas.controle_gastos.model.Transacao;
import com.financas.controle_gastos.model.Usuario;
import com.financas.controle_gastos.repository.TransacaoRepository;
import com.financas.controle_gastos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método auxiliar para pegar o usuário logado do Token JWT
    private Usuario getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String login;

        if (principal instanceof UserDetails) {
            login = ((UserDetails) principal).getUsername();
        } else {
            login = principal.toString();
        }

        // Certifique-se que o método no repositório corresponde ao campo do banco (username)
        return usuarioRepository.findByUsername(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + login));
    }

    public List<Transacao> listarTodas() {
        // AJUSTE: Busca apenas as transações do ID do usuário logado
        return repository.findByUsuarioId(getUsuarioLogado().getId());
    }

    public BigDecimal calcularSaldo() {
        // AJUSTE: Calcula o saldo apenas com as transações do usuário logado
        List<Transacao> transacoes = listarTodas();

        BigDecimal entradas = transacoes.stream()
                .filter(t -> t.getTipo().name().equals("ENTRADA"))
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saidas = transacoes.stream()
                .filter(t -> t.getTipo().name().equals("SAIDA"))
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return entradas.subtract(saidas);
    }

    public Transacao salvar(Transacao transacao) {
        // Busca o usuário logado do SecurityContext
        Usuario usuarioLogado = getUsuarioLogado();

        // Vincula o usuário à transação (ESSA PARTE É ESSENCIAL)
        transacao.setUsuario(usuarioLogado);

        return repository.save(transacao);
    }

    public Transacao editar(Long id, Transacao transacaoAtualizada) {
        return repository.findById(id).map(registro -> {
            // Segurança extra: Verifica se a transação pertence ao usuário logado
            if (!registro.getUsuario().getId().equals(getUsuarioLogado().getId())) {
                throw new RuntimeException("Acesso negado a esta transação.");
            }

            registro.setDescricao(transacaoAtualizada.getDescricao());
            registro.setValor(transacaoAtualizada.getValor());
            registro.setTipo(transacaoAtualizada.getTipo());
            registro.setClassificacao(transacaoAtualizada.getClassificacao());
            registro.setData(transacaoAtualizada.getData());

            return repository.save(registro);
        }).orElseThrow(() -> new RuntimeException("Transação não encontrada com o id: " + id));
    }

    public void excluir(Long id) {
        // Opcional: Adicionar verificação de segurança antes de deletar
        repository.deleteById(id);
    }
}