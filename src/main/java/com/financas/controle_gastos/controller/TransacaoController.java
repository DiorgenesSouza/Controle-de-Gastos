package com.financas.controle_gastos.controller;

import com.financas.controle_gastos.model.Transacao;
import com.financas.controle_gastos.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@CrossOrigin(origins = "http://localhost:4200") // Para o seu futuro Frontend Angular conectar sem problemas
public class TransacaoController {

    @Autowired
    private TransacaoService service; // Agora usamos a Service!

    @GetMapping
    public List<Transacao> listar() {
        return service.listarTodas();
    }

    @GetMapping("/saldo")
    public BigDecimal obterSaldo() {
        return service.calcularSaldo();
    }

    @PostMapping
    public Transacao salvar(@RequestBody Transacao transacao) {
        System.out.println(transacao);
        return service.salvar(transacao);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transacao> atualizar(@PathVariable Long id, @RequestBody Transacao transacao) {
        return ResponseEntity.ok(service.editar(id, transacao));
    }
}