package com.financas.controle_gastos.service;

import com.financas.controle_gastos.model.Transacao;
import com.financas.controle_gastos.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;

    public List<Transacao> listarTodas() {
        return repository.findAll();
    }

    public BigDecimal calcularSaldo() {
        List<Transacao> transacoes = repository.findAll();

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
        return repository.save(transacao);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}